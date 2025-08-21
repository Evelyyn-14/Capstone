package com.example.events_api.repository;

import com.example.events_api.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Find events by title containing a keyword (case-insensitive)
    List<Event> findByTitleContainingIgnoreCase(String title);
    
    // Find events by location containing a keyword (case-insensitive)
    List<Event> findByLocationContainingIgnoreCase(String location);
    
    // Find events within a date range
    @Query("SELECT e FROM Event e WHERE e.eventDateTime BETWEEN :startDate AND :endDate")
    List<Event> findEventsInDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    // Find upcoming events
    @Query("SELECT e FROM Event e WHERE e.eventDateTime > :currentTime ORDER BY e.eventDateTime ASC")
    List<Event> findUpcomingEvents(@Param("currentTime") LocalDateTime currentTime);
    
    // Find past events
    @Query("SELECT e FROM Event e WHERE e.eventDateTime < :currentTime ORDER BY e.eventDateTime DESC")
    List<Event> findPastEvents(@Param("currentTime") LocalDateTime currentTime);
}
