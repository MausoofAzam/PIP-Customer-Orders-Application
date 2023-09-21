package com.snort.service;

import com.snort.entity.Customer;
import com.snort.entity.OrderedItem;
import com.snort.exception.ex.*;
import com.snort.repository.CustomerRepository;
import com.snort.repository.OrderedItemRepository;
import com.snort.request.CustomerRequest;
import com.snort.request.OrderedItemRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.snort.enums.Constants.*;


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

    /**
     * @param customerRequest customer information
     * @return customer information
     * @throws CustomerCreationException it occurs when customer unable to create
     */
    public Customer createCustomerWithOrder(CustomerRequest customerRequest) {
        try {
//                    checking for unique Email and phone number in the DB
            boolean isPhoneNumberExist = customerRepository.existsByPhoneNumber(customerRequest.getPhoneNumber());
            log.info("phone number exist", isPhoneNumberExist);
            if (isPhoneNumberExist) {
                log.error("Phone number already exists");
                throw new PhoneNumberExistException(NUMBER_EXISTS.getValue());
            }
            boolean isEmailExist = customerRepository.existsByEmail(customerRequest.getEmail());
            if (isEmailExist) {
                log.error("Email  already exists");
                throw new EmailExistException(EMAIL_EXISTS.getValue());
            }
            Long nextCustomerId = findNextCustomerId();
            Customer customer = new Customer();
            //getting the updated customer id
            customer.setCustomerId(nextCustomerId);
            customer.setName(customerRequest.getName());
            customer.setEmail(customerRequest.getEmail());
            customer.setPhoneNumber(customerRequest.getPhoneNumber());
            customer.setAddress(customerRequest.getAddress());
            customer.setRegistrationDate(Instant.now());

            // Create OrderedItem entities from the OrderedItemRequest DTOs
            if (customerRequest.getOrders() != null) {
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
            throw new CustomerCreationException(ERROR_CREATING_CUSTOMER.getValue());
        }

    }

    private Long findNextCustomerId() {
        Long maxCustomerId = customerRepository.findMaxCustomerId();

        //if no customer exists, start with id 1; otherwise increment the maximum id
        return maxCustomerId != null ? maxCustomerId + 1 : 1L;
    }

    /**
     * @return list of customer information
     */
    @Override
    public List<Customer> findAllCustomerOrder() {
        return customerRepository.findAll();
    }

    //    pagination

    /**
     * @param pageable
     * @return returns List of Customer with page size 10
     */
    @Override
    public Page<Customer> findAllCustomerOrder(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    /**
     * @param customerId id of the customer
     * @return customer entity parameter
     */
    @Override
    public Customer getCustomerById(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            return customer.get();
        } else {
            throw new CustomerNotFoundException(CUSTOMER_NOT_FOUND.getValue() + customerId);
        }
    }

    /**
     * @param customerId id of the customer
     * @throws CustomerNotFoundException if the customer with given ID not found
     */

    @Override
    public void deleteCustomerById(Long customerId) {

        Optional<Long> maxCustomerIdOptional = Optional.ofNullable(customerRepository.findMaxCustomerId());
        Long nextAvailableCustomerId = maxCustomerIdOptional.orElse(1L);

        customerRepository.deleteById(customerId);

        // Reassigning the deleted ID to a new customer
        Optional<Customer> nextCustomerOptional = customerRepository.findById(nextAvailableCustomerId);
        if (nextCustomerOptional.isPresent()) {
            Customer nextCustomer = nextCustomerOptional.get();
            nextCustomer.setCustomerId(customerId);
            customerRepository.save(nextCustomer);
        }
    }


    /**
     * @param customerId      id of the customer
     * @param customerRequest details of the customer
     * @return customer details of  the customer entity
     * @throws CustomerNotFoundException it occurs when customer not found in the database
     */
    @Override
    public Customer updateCustomer(Long customerId, CustomerRequest customerRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            throw new CustomerNotFoundException(CUSTOMER_NOT_FOUND.getValue() + customerId);
        }
        Customer existingCustomer = optionalCustomer.get();
        existingCustomer.setName(customerRequest.getName());
        existingCustomer.setEmail(customerRequest.getEmail());
        existingCustomer.setPhoneNumber(customerRequest.getPhoneNumber());
        existingCustomer.setEmail(existingCustomer.getEmail());

        return customerRepository.save(existingCustomer);
    }

}
