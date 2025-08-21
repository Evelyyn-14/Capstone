package com.example.registration_api.controller;

import com.example.registration_api.entity.Registration;
import com.example.registration_api.repository.RegistrationRepository;
import com.example.registration_api.service.EventService;
import com.example.registration_api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "http://localhost:3000")
public class RegistrationController {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EventService eventService;

    // Helper method to extract JWT token from Authorization header
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getRegistrations(@RequestHeader("Authorization") String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        String username = jwtUtil.extractUsername(token);
        if (username == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Registration> registrations = registrationRepository.findByUsername(username);

        // Combine registration and event info
        List<Map<String, Object>> response = registrations.stream().map(reg -> {
            Map<String, Object> result = new HashMap<>();
            result.put("registration", reg);
            result.put("event", eventService.getEventById(reg.getEventId()));
            return result;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createRegistration(@RequestBody Registration reg,
                                                @RequestHeader("Authorization") String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        String username = jwtUtil.extractUsername(token);
        if (username == null) return ResponseEntity.status(401).body("Unauthorized");

        reg.setUsername(username);
        return ResponseEntity.ok(registrationRepository.save(reg));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegistration(@PathVariable Long id,
                                                @RequestBody Registration updated,
                                                @RequestHeader("Authorization") String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        String username = jwtUtil.extractUsername(token);
        if (username == null) return ResponseEntity.status(401).body("Unauthorized");

        Registration reg = registrationRepository.findById(id).orElse(null);

        if (reg == null || !reg.getUsername().equals(username)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        reg.setAttendeeName(updated.getAttendeeName());
        reg.setNotes(updated.getNotes());
        return ResponseEntity.ok(registrationRepository.save(reg));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegistration(@PathVariable Long id,
                                                @RequestHeader("Authorization") String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        String username = jwtUtil.extractUsername(token);
        if (username == null) return ResponseEntity.status(401).body("Unauthorized");

        Registration reg = registrationRepository.findById(id).orElse(null);

        if (reg == null || !reg.getUsername().equals(username)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        registrationRepository.delete(reg);
        return ResponseEntity.ok("Deleted");
    }
}