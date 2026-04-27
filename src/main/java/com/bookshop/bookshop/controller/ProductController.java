/*
 * Created by Yohanna Belay  on 2026.4.26
 * Copyright © 2026 Yohanna Belay. All rights reserved.
 */

package com.bookshop.bookshop.controller;

import com.bookshop.bookshop.model.Product;
import com.bookshop.bookshop.repository.ProductLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")

public class ProductController
{
    //this injects the Porduct Library into the class
    @Autowired
    private ProductLibrary productLibrary;

    //returns all products from the databse
    @GetMapping
    public List<Product> getAllProducts() {
        return productLibrary.findAll();
    }

    //returns a single product by its ID
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productLibrary.findById(id).orElse(null);
    }

    //searches products by title keyword
    @GetMapping("/search")
    public List<Product> search(@RequestParam String q) {
        return productLibrary.findByTitleContainingIgnoreCase(q);
    }

    //returns all products in a specific category
    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        return productLibrary.findByCategory(category);
    }

}