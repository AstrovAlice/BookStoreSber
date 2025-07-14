package com.bookStore.bookstore.configurations;

import com.bookStore.bookstore.model.User;
import com.bookStore.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles("ROLE_ADMIN");

            userRepository.save(admin);
            System.out.println("Администратор admin создан. Логин и пароль: admin");
        } else {
            System.out.println("Учётка администратора уже существует.");
            System.out.println("login: admin");
            System.out.println("password: admin");
        }
    }
}
