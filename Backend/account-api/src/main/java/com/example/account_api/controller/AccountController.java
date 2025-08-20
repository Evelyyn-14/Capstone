package main.java.com.example.account_api.controller;

import com.example.accountservice.dto.LoginRequest;
import com.example.accountservice.entity.User;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

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
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/token")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByName(request.getName())
            .filter(u -> u.getPassword().equals(request.getPassword()))
            .map(u -> ResponseEntity.ok(jwtUtil.generateToken(u.getName())))
            .orElse(ResponseEntity.status(401).body("Invalid username or password"));
    }
}