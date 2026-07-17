package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.model.request.CustomerRequest;
import com.example.demo.model.enums.CustomerStatus;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Long> createCustomer(@RequestBody CustomerRequest request) {
        Long id = customerService.createCustomer(request);
        return ResponseEntity.status(201).body(id);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Void> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerRequest request) {
        customerService.updateCustomer(customerId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{customerId}/activate")
    public ResponseEntity<Void> activateCustomer(@PathVariable Long customerId) {
        customerService.setStatus(customerId, CustomerStatus.ACTIVE);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{customerId}/deactivate")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable Long customerId) {
        customerService.setStatus(customerId, CustomerStatus.INACTIVE);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Customer>> listCustomers() {
        LOGGER.info("Listing customers");
        return ResponseEntity.ok(customerService.listCustomers());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long customerId) {
        LOGGER.info("Fetching customer {}", customerId);
        return customerService.getCustomerById(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lookup")
    public ResponseEntity<Customer> findByExternalId(@RequestParam String externalCustomerId) {
        LOGGER.info("Looking up customer by external id {}", externalCustomerId);
        return customerService.getCustomerByExternalId(externalCustomerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
