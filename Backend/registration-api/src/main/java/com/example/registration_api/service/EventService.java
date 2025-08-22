package com.example.registration_api.service;

import com.example.registration_api.dto.EventDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EventService {

    private final String eventApiBaseUrl = "http://events-api:8082/api/events/";

    public EventDTO getEventById(Long eventId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(eventApiBaseUrl + eventId, EventDTO.class);
    }
}
