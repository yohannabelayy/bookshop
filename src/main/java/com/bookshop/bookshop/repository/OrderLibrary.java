package com.bookshop.bookshop.repository;

import com.bookshop.bookshop.model.Order;
import com.bookshop.bookshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderLibrary extends JpaRepository<Order, Long> {

    // Find all orders placed by a specific user
    List<Order> findByUserOrderByCreatedAtDesc(User user);

}