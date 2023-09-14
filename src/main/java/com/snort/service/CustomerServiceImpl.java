package com.snort.service;

import com.snort.entity.Customer;
import com.snort.entity.OrderedItem;
import com.snort.exception.CustomerCreationException;
import com.snort.exception.CustomerNotFoundException;
import com.snort.exception.EmailExistException;
import com.snort.exception.PhoneNumberExistException;
import com.snort.repository.CustomerRepository;
import com.snort.repository.OrderedItemRepository;
import com.snort.request.CustomerRequest;
import com.snort.request.OrderedItemRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final OrderedItemRepository orderedItemRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, OrderedItemRepository orderedItemRepository) {
        this.customerRepository = customerRepository;
        this.orderedItemRepository = orderedItemRepository;
    }

    public Customer createCustomerWithOrder(CustomerRequest customerRequest) {
        // Create a Customer entity from the CustomerRequest DTO
        try {
//                    checking for unique Email and phone number in the DB
        boolean isPhoneNumberExist = customerRepository.existsByPhoneNumber(customerRequest.getPhoneNumber());
        log.info("phone number exist", isPhoneNumberExist);
        System.out.println("exist phone: " + isPhoneNumberExist);
        if (isPhoneNumberExist) {
            log.error("Phone number already exists");
            throw new PhoneNumberExistException("Phone Number already exist");
        }
        boolean isEmailExist = customerRepository.existsByEmail(customerRequest.getEmail());
        if (isEmailExist) {
            log.error("Email  already exists");
            throw new EmailExistException("Email already Exist");
        }

            Customer customer = new Customer();
            customer.setName(customerRequest.getName());
            customer.setEmail(customerRequest.getEmail());
            customer.setPhoneNumber(customerRequest.getPhoneNumber());
            customer.setAddress(customerRequest.getAddress());
            customer.setRegistrationDate(Instant.now());

            // Create OrderedItem entities from the OrderedItemRequest DTOs
            if (customerRequest.getOrders() !=null){
                List<OrderedItem> orderedItems = new ArrayList<>();
                for (OrderedItemRequest orderedItemRequest : customerRequest.getOrders()) {
                    OrderedItem orderedItem = new OrderedItem();
                    orderedItem.setId(UUID.randomUUID().toString());
                    orderedItem.setProductName(orderedItemRequest.getProductName());
                    orderedItem.setQuantity(orderedItemRequest.getQuantity());
                    orderedItem.setUnitPrice(orderedItemRequest.getUnitPrice());
                    orderedItem.setCustomer(customer); // Set the customer for the ordered item
                    orderedItems.add(orderedItem);
                }

                // Set the list of ordered items for the customer
                customer.setOrders(orderedItems);
            }
            // Save the customer and associated ordered items to the database
            customerRepository.save(customer);
            return customer;
        } catch (Exception e) {
            throw new CustomerCreationException("unable to create Customer and Order");
        }

    }

    @Override
    public List<Customer> findAllCustomerOrder() {

        List<Customer> customerList = customerRepository.findAll();
        return customerList;
    }

    //    pagination
    @Override
    public Page<Customer> findAllCustomerOrder(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public Customer getCustomerById(Long customerId) {
        try {
            Optional<Customer> customer = customerRepository.findById(customerId);
            return customer.get();
        } catch (Exception e) {
            throw new CustomerNotFoundException("Customer not found with id ::" + customerId);
        }
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
        } else {
            throw new CustomerNotFoundException("customer id not found, Unable to delete:" + customerId);
        }
    }

    @Override
    public Customer updateCustomer(Long customerId, CustomerRequest customerRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()){
            throw new CustomerNotFoundException("Customer Not found with id:"+customerId);
        }
        Customer existingCustomer =optionalCustomer.get();
        existingCustomer.setName(customerRequest.getName());
        existingCustomer.setEmail(customerRequest.getEmail());
        existingCustomer.setPhoneNumber(customerRequest.getPhoneNumber());
        existingCustomer.setEmail(existingCustomer.getEmail());

        return customerRepository.save(existingCustomer);
    }

}
