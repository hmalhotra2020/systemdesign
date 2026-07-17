package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dao.CustomerDao;
import com.example.demo.model.Customer;
import com.example.demo.model.enums.CustomerStatus;
import com.example.demo.model.request.CustomerRequest;

@Service
public class CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public Optional<Customer> getCustomerById(Long customerId) {
        LOGGER.info("Fetching customer by id {}", customerId);
        return customerDao.findById(customerId);
    }

    public Optional<Customer> getCustomerByExternalId(String externalCustomerId) {
        LOGGER.info("Fetching customer by external id {}", externalCustomerId);
        return customerDao.findByExternalId(externalCustomerId);
    }

    public List<Customer> listCustomers() {
        LOGGER.info("Listing all customers");
        return customerDao.findAll();
    }

    public Long createCustomer(CustomerRequest request) {
        LOGGER.info("Creating customer {}", request.externalCustomerId());
        return customerDao.insert(request.externalCustomerId(), request.email(), request.phoneNumber(), request.firstName(), request.lastName(), CustomerStatus.ACTIVE.name());
    }

    public void updateCustomer(Long customerId, CustomerRequest request) {
        LOGGER.info("Updating customer {}", customerId);
        customerDao.update(customerId, request.email(), request.phoneNumber(), request.firstName(), request.lastName());
    }

    public void setStatus(Long customerId, CustomerStatus status) {
        LOGGER.info("Setting status {} for customer {}", status, customerId);
        customerDao.updateStatus(customerId, status.name());
    }
}
