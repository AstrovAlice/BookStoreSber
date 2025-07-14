package com.bookStore.bookstore.controllers;

import com.bookStore.bookstore.model.User;
import com.bookStore.bookstore.repositories.UserServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserServiceRepository userService;

    @GetMapping
    public String dashboard(Model model) {
        long clientsCount = userService.countByRole("ROLE_USER");
        long employeesCount = userService.countByRole("ROLE_EMPLOYEE");
        long booksCount = userService.countBooks();

        model.addAttribute("clientsCount", clientsCount);
        model.addAttribute("employeesCount", employeesCount);
        model.addAttribute("booksCount", booksCount);
        return "admin/dashboard";
    }

    @GetMapping("/clients")
    public String clients(Model model, @RequestParam(required = false) String keyword) {
        List<User> clients = (keyword == null || keyword.isEmpty()) ?
                userService.findByRole("ROLE_USER") :
                userService.searchByRoleAndKeyword("ROLE_USER", keyword);
        model.addAttribute("clients", clients);
        model.addAttribute("keyword", keyword);
        return "admin/clients";
    }

    @GetMapping("/employees")
    public String employees(Model model, @RequestParam(required = false) String keyword) {
        List<User> employees = (keyword == null || keyword.isEmpty()) ?
                userService.findByRole("ROLE_EMPLOYEE") :
                userService.searchByRoleAndKeyword("ROLE_EMPLOYEE", keyword);
        model.addAttribute("employees", employees);
        model.addAttribute("keyword", keyword);
        return "admin/employees";
    }

    @PostMapping("/employees/add")
    public String addEmployee(@RequestParam String username, @RequestParam String password) {
        userService.registerEmployee(username, password);
        return "redirect:/admin/employees";
    }

    @PostMapping("/employees/delete/{id}")
    public String deleteEmployee(@RequestParam Long id) {
        System.out.println("delete: " + id);
        userService.unregisterEmployee(id);
        return "redirect:/admin/employees";
    }
}
