package com.snort.service;

import com.snort.entity.Customer;
import com.snort.request.CustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    Customer createCustomerWithOrder(CustomerRequest customerRequest);
    List<Customer> findAllCustomerOrder();
    Page<Customer> findAllCustomerOrder(Pageable pageable);
    Customer getCustomerById(Long customerId);
    void deleteCustomerById(Long customerId);
    Customer updateCustomer(Long customerId, CustomerRequest customerRequest);
}