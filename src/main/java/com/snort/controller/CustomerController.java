package com.snort.controller;

import com.snort.entity.Customer;
import com.snort.exception.CustomerCreationException;
import com.snort.exception.InvalidPageRequestException;
import com.snort.request.CustomerRequest;
import com.snort.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.snort.enums.Constants.DELETED_SUCCESSFULLY;
import static com.snort.enums.Constants.ERROR_CREATING_CUSTOMER;

@RestController
@RequestMapping("/api/customer")
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * @param customerRequest customer information
     * @return String message    information
     * @throws CustomerCreationException it occurs when customer unable to create
     */
    @PostMapping("/create")
    public ResponseEntity<String> createCustomerWithOrder(@Valid @RequestBody(required = false) CustomerRequest customerRequest) {
        try {
            if (customerRequest  == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("RequestBody is missing");
            }
            customerService.createCustomerWithOrder(customerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Customer and ordered items created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR_CREATING_CUSTOMER.getValue() + e.getMessage());
        }
    }

    /**
     * @param customerId
     * @return customer details
     */
    @GetMapping("customerId")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId) {
        log.info("Customer ID : {}", customerId);
        Customer customer = customerService.getCustomerById(customerId);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    /**
     * @return list of customer with orders
     */
    @GetMapping
    public ResponseEntity<List<Customer>> findAllCustomerOrders() {
        List<Customer> customerOrderList = customerService.findAllCustomerOrder();
        log.info("customerOrderList :{}", customerOrderList);
        return new ResponseEntity<>(customerOrderList, HttpStatus.OK);
    }

    /**
     * @param page pagination page number
     * @param size pagination default size
     * @return return List of Customer Page wise
     */

    @GetMapping("/getAll")
    public Page<Customer> getAllCustomer(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("page : {}, ", page);
        log.info("size : {}, ", size);
        PageRequest pageable = PageRequest.of(page, size);
        log.info("List of All Customer : {}", pageable);
        return customerService.findAllCustomerOrder(pageable);
    }

    /**
     * @param customerId
     * @return customer
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerDetailsById(@PathVariable Long customerId) {
        log.info("CustomerId : {} ", customerId);

        Customer customerDetails = customerService.getCustomerById(customerId);
        log.info("CustomerDetails : {}", customerDetails);
        return new ResponseEntity<>(customerDetails, HttpStatus.OK);
    }

    /**
     * @param customerId
     * @return it will return status code as HTTP.Status as 204
     */
    @DeleteMapping("delete/{customerId}")
    public ResponseEntity<String> deleteCustomerWithOrder(@PathVariable Long customerId) {
        log.info("customerId : {}", customerId);
        customerService.deleteCustomerById(customerId);
        log.info("Customer with id Deleted");
        return new ResponseEntity<>(DELETED_SUCCESSFULLY.getValue(), HttpStatus.NO_CONTENT);
    }

    /**
     * @param customerId
     * @param customerRequest
     * @return it will return only customer details
     */
    @PutMapping("/update/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerRequest customerRequest) {
        log.info("Request payload  customerId: {}", customerId);
        log.info("Request Payload :{}", customerRequest);
        Customer customer = customerService.updateCustomer(customerId, customerRequest);
        log.info("updated Customer : {}", customer);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }
}
