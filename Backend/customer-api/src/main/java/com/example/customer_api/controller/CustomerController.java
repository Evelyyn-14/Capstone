package com.example.customer_api.controller;

import com.example.customer_api.model.Customer;
import com.example.customer_api.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")   // allow frontend access
public class CustomerController {

    @Autowired
    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    // GET all
    @GetMapping
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        return ResponseEntity.of(repository.findById(id));
    }
    

    // POST (create new)
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return repository.save(customer);
    }

    // PUT (update existing)
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updated) {
        return repository.findById(id).map(customer -> {
            customer.setName(updated.getName());
            customer.setEmail(updated.getEmail());
            customer.setPassword(updated.getPassword());
            return ResponseEntity.ok(repository.save(customer));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            if (!repository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error deleting customer with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lookup by name
    @PostMapping(value = "/byname")
    public ResponseEntity<Customer> getCustomerByName(@RequestBody String name) {
        return repository.findByName(name.trim())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
