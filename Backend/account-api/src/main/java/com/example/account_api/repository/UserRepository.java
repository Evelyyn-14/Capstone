package main.java.com.example.account_api.repository;

import java.util.Optional;

import main.java.com.example.account_api.entity.User;

public public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    boolean existsByName(String name);
} 
