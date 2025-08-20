package com.example.account_api.controller;

import com.example.account_api.dto.LoginRequest;
import com.example.account_api.entity.User;
import com.example.account_api.repository.UserRepository;
import com.example.account_api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public String root() {
        return "Account service is running.";
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }
        // Hash the password before saving!
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/token")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByName(request.getName())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getName());
                    return ResponseEntity.ok(token); // Send token in response
                })
                .orElse(ResponseEntity.status(401).body("Invalid username or password"));
    }
}