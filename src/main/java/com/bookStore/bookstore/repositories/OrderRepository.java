package com.bookStore.bookstore.repositories;

import com.bookStore.bookstore.model.Order;
import com.bookStore.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
