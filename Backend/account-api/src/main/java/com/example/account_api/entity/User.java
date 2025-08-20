package com.example.account_api.entity;


import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String email;

    private String password; // store hashed password

    // getters and setters
}
