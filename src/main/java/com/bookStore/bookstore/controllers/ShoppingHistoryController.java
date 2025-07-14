package com.bookStore.bookstore.controllers;

import com.bookStore.bookstore.repositories.BookRepository;
import com.bookStore.bookstore.model.Order;
import com.bookStore.bookstore.repositories.OrderRepository;
import com.bookStore.bookstore.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;

@Controller
public class ShoppingHistoryController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/orders")
    public String viewOrders(@AuthenticationPrincipal User user, Model model) {
        List<Order> orders = orderRepository.findByUser(user);
        model.addAttribute("orders", orders);
        return "shoppingHistory";
    }
}
