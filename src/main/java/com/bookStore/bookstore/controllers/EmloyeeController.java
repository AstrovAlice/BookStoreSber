package com.bookStore.bookstore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class EmloyeeController {

    @GetMapping("/employee")
    public String emloyeeDashboard(){
        return "employee";
    }
}
