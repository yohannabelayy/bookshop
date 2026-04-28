/*
 * Created by Yohanna Belay  on 2026.4.27
 * Copyright © 2026 Yohanna Belay. All rights reserved.
 */

package com.bookshop.bookshop.controller;

import com.bookshop.bookshop.model.Order;
import com.bookshop.bookshop.model.Product;
import com.bookshop.bookshop.model.User;
import com.bookshop.bookshop.repository.OrderLibrary;
import com.bookshop.bookshop.repository.ProductLibrary;
import com.bookshop.bookshop.repository.UserLibrary;
import com.bookshop.bookshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderLibrary orderLibrary;

    @Autowired
    private ProductLibrary productLibrary;

    @Autowired
    private UserLibrary userLibrary;

    @Autowired
    private JwtUtil jwtUtil;

    // Place a new order
    @PostMapping
    public ResponseEntity<?> placeOrder(
            @RequestBody Map<String, Object> body,
            @RequestHeader("Authorization") String authHeader) {

        // Get the JWT token from the header and extract email
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        // Find the user by email
        Optional<User> userOptional = userLibrary.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOptional.get();

        // Get the list of items from the request body
        List<Map<String, Object>> items =
                (List<Map<String, Object>>) body.get("items");

        // Calculate total price
        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            int quantity = Integer.parseInt(item.get("quantity").toString());

            Optional<Product> productOptional = productLibrary.findById(productId);
            if (productOptional.isPresent()) {
                BigDecimal itemTotal = productOptional.get().getPrice()
                        .multiply(BigDecimal.valueOf(quantity));
                total = total.add(itemTotal);
            }
        }

        // Create and save the order
        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        orderLibrary.save(order);

        // Send back confirmation
        return ResponseEntity.ok(Map.of(
                "orderId", order.getId(),
                "total", total,
                "status", "PENDING"
        ));
    }

    // Get all orders for the logged in user
    @GetMapping("/my")
    public ResponseEntity<?> myOrders(
            @RequestHeader("Authorization") String authHeader) {

        // Extract email from token
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        // Find user and return their orders
        Optional<User> userOptional = userLibrary.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<Order> orders = orderLibrary
                .findByUserOrderByCreatedAtDesc(userOptional.get());
        return ResponseEntity.ok(orders);
    }
}