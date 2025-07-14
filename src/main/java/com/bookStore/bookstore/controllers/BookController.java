package com.bookStore.bookstore.controllers;

import com.bookStore.bookstore.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/{id}/increase")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String increaseQuantity(@PathVariable Long id, @RequestParam int amount) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setQuantity(book.getQuantity() + Math.max(0, amount));
            bookRepository.save(book);
        });
        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/decrease")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String decreaseQuantity(@PathVariable Long id, @RequestParam int amount) {
        bookRepository.findById(id).ifPresent(book -> {
            int newQty = Math.max(0, book.getQuantity() - amount);
            book.setQuantity(newQty);
            bookRepository.save(book);
        });
        return "redirect:/books/" + id;
    }
}
