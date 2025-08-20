package com.example.account_api.repository;

import java.util.Optional;

import com.example.account_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository<User> extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    boolean existsByName(String name);
} 
