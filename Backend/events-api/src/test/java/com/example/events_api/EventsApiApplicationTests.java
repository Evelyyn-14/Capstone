package com.example.events_api;

import com.example.events_api.model.Event;
import com.example.events_api.repository.EventRepository;
import com.example.events_api.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventsApiApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private EventRepository eventRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String baseUrl;
    private Event sampleEvent;
    private HttpHeaders authHeaders;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/events";

        sampleEvent = new Event();
        sampleEvent.setId(1L);
        sampleEvent.setTitle("Spring Boot Workshop");
        sampleEvent.setEventDateTime(LocalDateTime.now().plusDays(1));
        sampleEvent.setLocation("Online");

        // Create JWT token for authentication
        String token = jwtUtil.generateToken("testuser");
        authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + token);
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void testHealth() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/health", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Events service is running.");
    }

    @Test
    void testGetAllEvents() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(sampleEvent));

        HttpEntity<String> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<Event[]> response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, Event[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getTitle()).isEqualTo("Spring Boot Workshop");
    }

    @Test
    void testGetEventByIdFound() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        HttpEntity<String> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<Event> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.GET, entity, Event.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Spring Boot Workshop");
    }

    @Test
    void testGetEventByIdNotFound() {
        Mockito.when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        HttpEntity<String> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<Event> response = restTemplate.exchange(baseUrl + "/99", HttpMethod.GET, entity, Event.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateEvent() {
        Event newEvent = new Event();
        newEvent.setTitle("New Event");
        newEvent.setEventDateTime(LocalDateTime.now().plusDays(3));
        newEvent.setLocation("Boston");

        Mockito.when(eventRepository.save(any(Event.class)))
                .thenAnswer(inv -> {
                    Event e = inv.getArgument(0);
                    e.setId(10L);
                    return e;
                });

        HttpEntity<Event> request = new HttpEntity<>(newEvent, authHeaders);
        ResponseEntity<Event> response = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Event.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isEqualTo(10L);
        assertThat(response.getBody().getTitle()).isEqualTo("New Event");
    }

    @Test
    void testUpdateEventFound() {
        Event update = new Event();
        update.setTitle("Updated Title");
        update.setEventDateTime(LocalDateTime.now().plusDays(5));
        update.setLocation("Updated Location");

        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        Mockito.when(eventRepository.save(any(Event.class))).thenReturn(sampleEvent);

        HttpEntity<Event> request = new HttpEntity<>(update, authHeaders);
        ResponseEntity<Event> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.PUT, request, Event.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void testUpdateEventNotFound() {
        Mockito.when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        Event update = new Event();
        update.setTitle("Doesn't Matter");
        update.setEventDateTime(LocalDateTime.now().plusDays(1));
        update.setLocation("Doesn't Matter");

        HttpEntity<Event> request = new HttpEntity<>(update, authHeaders);
        ResponseEntity<Event> response = restTemplate.exchange(baseUrl + "/99", HttpMethod.PUT, request, Event.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteEventFound() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        HttpEntity<String> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.DELETE, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testDeleteEventNotFound() {
        Mockito.when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        HttpEntity<String> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/99", HttpMethod.DELETE, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testSearchByTitle() {
        Mockito.when(eventRepository.findByTitleContainingIgnoreCase("Spring"))
                .thenReturn(List.of(sampleEvent));

        HttpEntity<String> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<Event[]> response = restTemplate.exchange(
                baseUrl + "/search/title?title=Spring", HttpMethod.GET, entity, Event[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()[0].getTitle()).isEqualTo("Spring Boot Workshop");
    }

    @Test
    void testSearchByLocation() {
        Mockito.when(eventRepository.findByLocationContainingIgnoreCase("Online"))
                .thenReturn(List.of(sampleEvent));

        HttpEntity<String> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<Event[]> response = restTemplate.exchange(
                baseUrl + "/search/location?location=Online", HttpMethod.GET, entity, Event[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()[0].getLocation()).isEqualTo("Online");
    }

    @Test
    void testEventsInRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Mockito.when(eventRepository.findEventsInDateRange(eq(start), eq(end)))
                .thenReturn(List.of(sampleEvent));

        HttpEntity<String> entity = new HttpEntity<>(authHeaders);
        ResponseEntity<Event[]> response = restTemplate.exchange(
                baseUrl + "/range?startDate=" + start + "&endDate=" + end, HttpMethod.GET, entity, Event[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()[0].getTitle()).isEqualTo("Spring Boot Workshop");
    }
}
