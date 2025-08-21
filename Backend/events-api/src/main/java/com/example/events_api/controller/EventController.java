package com.example.events_api.controller;

import com.example.events_api.model.Event;
import com.example.events_api.repository.EventRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // Health check endpoint (no authentication required)
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Events service is running.");
    }

    // Get all events
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    // Get upcoming events
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> events = eventRepository.findUpcomingEvents(LocalDateTime.now());
        return ResponseEntity.ok(events);
    }

    // Get past events
    @GetMapping("/past")
    public ResponseEntity<List<Event>> getPastEvents() {
        List<Event> events = eventRepository.findPastEvents(LocalDateTime.now());
        return ResponseEntity.ok(events);
    }

    // Get a specific event by ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventRepository.findById(id);
        
        if (event.isPresent()) {
            return ResponseEntity.ok(event.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new event
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        event.setId(null); // Ensure ID is null for new events
        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    // Update an existing event
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody Event eventDetails) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        
        if (eventOptional.isPresent()) {
            Event existingEvent = eventOptional.get();
            
            // Update the event details
            existingEvent.setTitle(eventDetails.getTitle());
            existingEvent.setLocation(eventDetails.getLocation());
            existingEvent.setEventDateTime(eventDetails.getEventDateTime());
            
            Event updatedEvent = eventRepository.save(existingEvent);
            return ResponseEntity.ok(updatedEvent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete an event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        
        if (eventOptional.isPresent()) {
            eventRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Search events by title
    @GetMapping("/search/title")
    public ResponseEntity<List<Event>> searchEventsByTitle(@RequestParam String title) {
        List<Event> events = eventRepository.findByTitleContainingIgnoreCase(title);
        return ResponseEntity.ok(events);
    }

    // Search events by location
    @GetMapping("/search/location")
    public ResponseEntity<List<Event>> searchEventsByLocation(@RequestParam String location) {
        List<Event> events = eventRepository.findByLocationContainingIgnoreCase(location);
        return ResponseEntity.ok(events);
    }

    // Get events within a date range
    @GetMapping("/range")
    public ResponseEntity<List<Event>> getEventsInDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        List<Event> events = eventRepository.findEventsInDateRange(start, end);
        return ResponseEntity.ok(events);
    }

    // Exception handler for validation errors
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        return ResponseEntity.badRequest().body("Validation errors: " + errors.toString());
    }
}
