package com.bookStore.bookstore.controllers;

import com.bookStore.bookstore.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "home";
    }
}
