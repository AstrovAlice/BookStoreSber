package com.bookStore.bookstore.services;

import com.bookStore.bookstore.model.User;
import com.bookStore.bookstore.repositories.BookRepository;
import com.bookStore.bookstore.repositories.UserRepository;
import com.bookStore.bookstore.repositories.UserServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserServiceRepository {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public long countByRole(String role) {
        return userRepository.countByRoles(role);
    }

    @Override
    public long countBooks() {
        return bookRepository.count();
    }

    @Override
    public List<User> findByRole(String role) {
        return userRepository.findByRoles(role);
    }

    @Override
    public List<User> searchByRoleAndKeyword(String role, String keyword) {
        return userRepository.findByRolesAndUsernameContainingIgnoreCase(role, keyword);
    }

    @Override
    public void registerEmployee(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles("ROLE_EMPLOYEE");
        userRepository.save(user);
    }

    @Override
    public void unregisterEmployee(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (!user.getRoles().equals("ROLE_EMPLOYEE")) {
            throw new IllegalArgumentException("User with id " + id + " is not an employee");
        }

        userRepository.delete(user);
    }
}
