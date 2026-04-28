/*
 * Created by Yohanna Belay  on 2026.4.27
 * Copyright © 2026 Yohanna Belay. All rights reserved.
 */

package com.bookshop.bookshop.controller;

import com.bookshop.bookshop.model.User;
import com.bookshop.bookshop.repository.UserLibrary;
import com.bookshop.bookshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthorizerController {

    @Autowired
    private UserLibrary userLibrary;

    @Autowired
    private JwtUtil jwtUtil;

    // BCrypt encoder for hashing passwords
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Signup endpoint — creates a new user account
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {

        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");

        // Check if email is already registered
        if (userLibrary.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // Create new user and hash the password
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Save user to database
        userLibrary.save(user);

        // Generate token and send back
        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", email,
                "name", name
        ));
    }

    // Login endpoint — checks credentials and returns token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        // Find user by email
        Optional<User> userOptional = userLibrary.findByEmail(email);

        // If user not found or password wrong — reject
        if (userOptional.isEmpty() ||
                !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        // Generate token and send back
        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", email,
                "name", userOptional.get().getName()
        ));
    }
}