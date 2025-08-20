package com.example.account_api.repository;

import java.util.Optional;

import com.example.account_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name); // this must return Optional<User>
}
