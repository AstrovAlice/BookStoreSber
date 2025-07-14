package com.bookStore.bookstore.repositories;

import com.bookStore.bookstore.model.User;
import java.util.List;

public interface UserServiceRepository {
    long countByRole(String role);
    long countBooks();
    List<User> findByRole(String role);
    List<User> searchByRoleAndKeyword(String role, String keyword);
    void registerEmployee(String username, String password);
    void unregisterEmployee(Long id);
}
