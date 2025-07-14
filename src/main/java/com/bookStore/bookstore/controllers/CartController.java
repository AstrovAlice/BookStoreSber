package com.bookStore.bookstore.controllers;

import com.bookStore.bookstore.model.Book;
import com.bookStore.bookstore.model.Order;
import com.bookStore.bookstore.model.User;
import com.bookStore.bookstore.repositories.BookRepository;
import com.bookStore.bookstore.repositories.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        Map<Book, Integer> cartItems = new LinkedHashMap<>();

        if (cart != null) {
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                bookRepository.findById(entry.getKey()).ifPresent(book ->
                        cartItems.put(book, entry.getValue())
                );
            }
        }

        double total = cartItems.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "cart";
    }


    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        Map<Long, Integer> cartMap = (Map<Long, Integer>) session.getAttribute("cart");
        if (cartMap != null && cartMap.containsKey(id)) {
            int qty = cartMap.get(id);
            if (qty > 1) {
                cartMap.put(id, qty - 1); // уменьшаем на 1
            } else {
                cartMap.remove(id); // полностью удаляем
            }
        }

        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, @AuthenticationPrincipal User user) {
        Map<Long, Integer> cartMap = (Map<Long, Integer>) session.getAttribute("cart");

        if (cartMap != null && !cartMap.isEmpty()) {
            Order order = new Order();
            order.setUser(user);
            order.setItems(new HashMap<>(cartMap));

            double total = 0;
            for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
                Book book = bookRepository.findById(entry.getKey()).orElse(null);
                if (book != null) {
                    total += book.getPrice() * entry.getValue();
                    book.setQuantity(book.getQuantity() - entry.getValue()); // уменьшаем количество на складе
                    bookRepository.save(book);
                }
            }

            order.setTotalPrice(total);
            order.setStatus("Ожидает"); // можно потом добавить обработку
            order.setCreatedAt(java.time.LocalDateTime.now());

            orderRepository.save(order); // сохраняем заказ
        }

        session.removeAttribute("cart");
        return "redirect:/cart?success";
    }

    @Autowired
    private OrderRepository orderRepository;


    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id,
                            HttpSession session,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {

        Map<Long, Integer> cartMap = (Map<Long, Integer>) session.getAttribute("cart");
        if (cartMap == null) {
            cartMap = new HashMap<>();
        }

        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            int currentQty = cartMap.getOrDefault(id, 0);
            if (currentQty < book.getQuantity()) {
                cartMap.put(id, currentQty + 1);
                redirectAttributes.addFlashAttribute("success", "Книга в корзине.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Больше книг на складе нет.");
            }
        }

        session.setAttribute("cart", cartMap);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


}
