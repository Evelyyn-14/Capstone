package com.example.registration_api.service;

import com.example.registration_api.dto.EventDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class EventService {

    private final String eventApiBaseUrl = "http://events-api:8082/api/events/";

    public EventDTO getEventById(Long eventId, String jwtToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);  // ✅ add JWT here
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<EventDTO> response = restTemplate.exchange(
                eventApiBaseUrl + eventId,
                HttpMethod.GET,
                requestEntity,
                EventDTO.class
        );

        return response.getBody();
    }

}
