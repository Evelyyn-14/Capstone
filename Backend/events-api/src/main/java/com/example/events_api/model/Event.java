package com.example.events_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Event title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String title;
    
    @NotNull(message = "Event date and time is required")
    @Column(name = "event_date_time", nullable = false)
    private LocalDateTime eventDateTime;
    
    @NotBlank(message = "Event location is required")
    @Size(max = 300, message = "Location cannot exceed 300 characters")
    @Column(nullable = false, length = 300)
    private String location;
    
    @PrePersist
    protected void onCreate() {
        // No additional fields to set on creation
    }
    
    @PreUpdate
    protected void onUpdate() {
        // No additional fields to set on update
    }
    
    // Constructor without id (for creation)
    public Event(String title, LocalDateTime eventDateTime, String location) {
        this.title = title;
        this.eventDateTime = eventDateTime;
        this.location = location;
    }
}
