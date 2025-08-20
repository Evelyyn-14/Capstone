package com.example.account_api.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String name;
    private String password;
} 
