package com.example.account_api;

import com.example.account_api.dto.LoginRequest;
import com.example.account_api.entity.User;
import com.example.account_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountApiApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/account";
        userRepository.deleteAll(); // clean database between tests
    }

    @Test
    void contextLoads() {
        // sanity check
    }

    @Test
    void root_ShouldReturnServiceRunning() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Account service is running.");
    }

    @Test
    void register_ShouldCreateUser() {
        User newUser = new User();
        newUser.setName("alice");
        newUser.setPassword("mypassword");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/register", newUser, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User registered successfully.");
        assertThat(userRepository.findByName("alice")).isPresent();
    }

    @Test
    void register_ShouldFailWhenUserExists() {
        User existing = new User();
        existing.setName("john");
        existing.setPassword("pass");
        userRepository.save(existing);

        User duplicate = new User();
        duplicate.setName("john");
        duplicate.setPassword("anotherPass");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/register", duplicate, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Username already exists.");
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        User user = new User();
        user.setName("john");
        user.setPassword("password123");
        restTemplate.postForEntity(baseUrl + "/register", user, String.class);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setName("john");
        loginRequest.setPassword("password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/token", loginRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotBlank(); // token returned
    }

    @Test
    void login_ShouldFail_WhenWrongPassword() {
        User user = new User();
        user.setName("john");
        user.setPassword("password123");
        restTemplate.postForEntity(baseUrl + "/register", user, String.class);

        LoginRequest badLogin = new LoginRequest();
        badLogin.setName("john");
        badLogin.setPassword("wrong");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/token", badLogin, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Invalid username or password");
    }

    @Test
    void login_ShouldFail_WhenUserNotFound() {
        LoginRequest badLogin = new LoginRequest();
        badLogin.setName("ghost");
        badLogin.setPassword("whatever");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/token", badLogin, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Invalid username or password");
    }
}
