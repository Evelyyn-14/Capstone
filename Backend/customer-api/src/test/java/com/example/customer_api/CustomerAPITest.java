package com.example.customer_api;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import com.example.customer_api.model.Customer;
import com.example.customer_api.repository.CustomerRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomerAPITest {

    @LocalServerPort
    private int port;

    @Autowired 
    TestRestTemplate template;

    @Autowired
    private CustomerRepository repository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/customers";
        repository.deleteAll(); 
    }

    @Test
    void testGetAllCustomers() {
        Customer c1 = repository.save(new Customer("Alice", "alice@email.com", "secret"));
        Customer c2 = repository.save(new Customer("Bob", "bob@email.com", "secret"));

        ResponseEntity<Customer[]> response = template.getForEntity(baseUrl, Customer[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(2);
    }

    @Test
    void testGetCustomerById() {
        Customer saved = repository.save(
                new Customer("Alice", "alice@email.com", "secret")
        );

        String url = "http://localhost:" + port + "/api/customers/" + saved.getId();
        ResponseEntity<Customer> response = template.getForEntity(url, Customer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(saved.getId());
        assertThat(response.getBody().getName()).isEqualTo("Alice");
    }

    @Test
    void testCreateCustomer() {
        Customer newCustomer = new Customer("Alice", "alice@email.com", "secret");

        ResponseEntity<Customer> response =
                template.postForEntity(baseUrl, newCustomer, Customer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();  // should be generated
        assertThat(response.getBody().getName()).isEqualTo("Alice");

        Customer saved = repository.findById(response.getBody().getId()).orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("alice@email.com");
    }

    @Test
    void testUpdateCustomer() {
        Customer saved = repository.save(new Customer("Alice", "alice@email.com", "secret"));

        Customer updated = new Customer("Alice Updated", "alice.updated@email.com", "newpass");

        HttpEntity<Customer> request = new HttpEntity<>(updated);
        ResponseEntity<Customer> response =
                template.exchange(baseUrl + "/" + saved.getId(), HttpMethod.PUT, request, Customer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Alice Updated");

        Customer fromDb = repository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getEmail()).isEqualTo("alice.updated@email.com");
    }

    @Test
    void testDeleteCustomer() {
        Customer saved = repository.save(new Customer("Bob", "bob@email.com", "secret"));

        ResponseEntity<Void> response =
                template.exchange(baseUrl + "/" + saved.getId(), HttpMethod.DELETE, null, Void.class);

        // Enhanced error reporting
        if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
            System.out.println("Delete failed with status: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Customer ID: " + saved.getId());
            System.out.println("URL: " + baseUrl + "/" + saved.getId());
        }

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(repository.existsById(saved.getId())).isFalse();
    }

       @Test
    void testGetCustomerByName_found() {
        Customer saved = repository.save(new Customer("Alice", "alice@email.com", "secret"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<>("Alice", headers);

        ResponseEntity<Customer> response = template.postForEntity(baseUrl + "/byname", request, Customer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(saved.getId());
        assertThat(response.getBody().getName()).isEqualTo("Alice");
    }
}
