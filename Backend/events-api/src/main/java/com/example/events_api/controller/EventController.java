package com.example.events_api.controller;

import com.example.events_api.model.Event;
import com.example.events_api.repository.EventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    @Autowired
    private final EventRepository repo;

    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Event create(@RequestBody Event event) {
        return repo.save(event);
    }

    // PUT (update existing)
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event updated) {
        return repo.findById(id).map(event -> {
            event.setTitle(updated.getTitle());
            event.setEventDateTime(updated.getEventDateTime());
            event.setLocation(updated.getLocation());
            return ResponseEntity.ok(repo.save(event));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        try {
            if (!repo.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error deleting event with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
