package com.bookStore.bookstore.controllers;

import com.bookStore.bookstore.model.Book;
import com.bookStore.bookstore.repositories.BookRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/books")
public class BookWebController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public String listBook(Model model, HttpSession session) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);

        // Количество книг в корзине
        List<Book> cart = (List<Book>) session.getAttribute("cart");
        Map<Long, Integer> cartMap = new HashMap<>();
        if (cart != null) {
            for (Book b : cart) {
                cartMap.put(b.getId(), cartMap.getOrDefault(b.getId(), 0) + 1);
            }
        }

        int cartSize = cart != null ? cart.size() : 0;
        model.addAttribute("cartMap", cartMap);
        model.addAttribute("cartSize", cartSize);

        return "list";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid book id: " + id));
        model.addAttribute("book", book);
        return "add-book";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String viewBookDetails(@PathVariable Long id, Model model, HttpSession session) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Книга не найдена: " + id));
        model.addAttribute("book", book);

        // Получаем карту корзины
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        int quantityInCart = 0;
        if (cart != null) {
            quantityInCart = cart.getOrDefault(book.getId(), 0);
        }

        model.addAttribute("quantityInCart", quantityInCart);
        return "details";
    }



    @PostMapping(value = "/save")
    public String saveBook(@ModelAttribute Book book, @RequestParam("coverImage") MultipartFile file) throws IOException {
        String uploadDir = new File("uploads").getAbsolutePath() + File.separator;
        String filePath;

        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            file.transferTo(new File(uploadDir + fileName));
            filePath = "/uploads/" + fileName;
        } else {
            filePath = "/uploads/default-cover.jpg";
        }

        book.setCoverImagePath(filePath);
        bookRepository.save(book);
        Long id = book.getId();
        return "redirect:/books/" + id;
    }


}
