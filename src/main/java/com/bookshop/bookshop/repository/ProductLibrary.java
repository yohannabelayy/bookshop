/*
 * Created by Yohanna Belay  on 2026.4.25
 * Copyright © 2026 Yohanna Belay. All rights reserved.
 */

package com.bookshop.bookshop.repository;

import com.bookshop.bookshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductLibrary extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByTitleContainingIgnoreCase(String title);

}