package com.bookStore.bookstore.repositories;

import com.bookStore.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    long countByRoles(String role);
    List<User> findByRoles(String role);
    boolean existsByUsername(String username);
    List<User> findByRolesAndUsernameContainingIgnoreCase(String role, String keyword);

}
