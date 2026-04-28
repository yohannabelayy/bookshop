/*
 * Created by Yohanna Belay  on 2026.4.27
 * Copyright © 2026 Yohanna Belay. All rights reserved.
 */

package com.bookshop.bookshop.repository;

import com.bookshop.bookshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserLibrary extends JpaRepository<User, Long>
{
    //to find user by email
    Optional<User>findByEmail(String email);

    //to make sure the email is unique
    boolean existsByEmail(String email);
}
