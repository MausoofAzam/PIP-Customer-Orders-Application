package com.snort.service;


import com.snort.entity.Customer;
import com.snort.entity.OrderedItem;
import com.snort.exception.ex.CustomerCreationException;
import com.snort.exception.ex.CustomerNotFoundException;
import com.snort.repository.CustomerRepository;
import com.snort.repository.OrderedItemRepository;
import com.snort.request.CustomerRequest;
import com.snort.request.OrderedItemRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private OrderedItemRepository orderedItemRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Optional<Customer> optionalCustomer;
    private Customer customer;
    private List<Customer> customerList;

    //The @BeforeEach annotation in JUnit is used to signal that the
    // annotated method should be executed before each test method in the test class.
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private CustomerRequest createSampleCustomerRequest() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("Mausoof");
        customerRequest.setEmail("azam@xyz.com");
        customerRequest.setPhoneNumber("7845545155");
        customerRequest.setAddress("Bangalore");

        OrderedItemRequest orderedItemRequest = new OrderedItemRequest();
        orderedItemRequest.setQuantity(10);
        orderedItemRequest.setProductName("Iphone");
        orderedItemRequest.setUnitPrice(BigDecimal.valueOf(1500.01));

        return customerRequest;

    }


    @Test
    void createCustomerWithOrder_shouldThrowEmailExistException() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        CustomerRequest customerRequest = createSampleCustomerRequest();
        Assertions.assertThrows(CustomerCreationException.class, () -> {
            customerService.createCustomerWithOrder(customerRequest);
        });
    }

    @Test
    void createCustomerWithOrder_shouldThrowPhoneExistException() {
        when(customerRepository.existsByPhoneNumber(anyString())).thenReturn(true);

        CustomerRequest customerRequest = createSampleCustomerRequest();
        Assertions.assertThrows(CustomerCreationException.class, () -> {
            customerService.createCustomerWithOrder(customerRequest);
        });
    }
    @Test
    void testCreateCustomerWithOrderAndOrderedItems() {
        // Mock customer request
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("test");
        customerRequest.setEmail("test@abc.com");
        customerRequest.setPhoneNumber("1234567890");
        customerRequest.setAddress("Bangalore");

        // Mock ordered item request
        OrderedItemRequest orderedItemRequest1 = new OrderedItemRequest();
        orderedItemRequest1.setProductName("Product1");
        orderedItemRequest1.setQuantity(2);
        orderedItemRequest1.setUnitPrice(BigDecimal.valueOf(20.0));

        OrderedItemRequest orderedItemRequest2 = new OrderedItemRequest();
        orderedItemRequest2.setProductName("Product2");
        orderedItemRequest2.setQuantity(3);
        orderedItemRequest2.setUnitPrice(BigDecimal.valueOf(15.0));

        List<OrderedItemRequest> orderedItemRequests = new ArrayList<>();
        orderedItemRequests.add(orderedItemRequest1);
        orderedItemRequests.add(orderedItemRequest2);

        customerRequest.setOrders(orderedItemRequests);

        // Mock customer entity
        Customer customerEntity = new Customer();
        customerEntity.setCustomerId(1L);
        customerEntity.setName(customerRequest.getName());
        customerEntity.setEmail(customerRequest.getEmail());
        customerEntity.setPhoneNumber(customerRequest.getPhoneNumber());
        customerEntity.setAddress(customerRequest.getAddress());

        // Mock ordered item entities
        List<OrderedItem> orderedItemEntities = new ArrayList<>();
        for (OrderedItemRequest orderedItemRequest : orderedItemRequests) {
            OrderedItem orderedItemEntity = new OrderedItem();
            orderedItemEntity.setId(UUID.randomUUID().toString());
            orderedItemEntity.setProductName(orderedItemRequest.getProductName());
            orderedItemEntity.setQuantity(orderedItemRequest.getQuantity());
            orderedItemEntity.setUnitPrice(orderedItemRequest.getUnitPrice());
            orderedItemEntity.setCustomer(customerEntity);

            orderedItemEntities.add(orderedItemEntity);
        }

        when(customerRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any())).thenReturn(customerEntity);
        when(orderedItemRepository.saveAll(any())).thenReturn(orderedItemEntities);

        Customer result = customerService.createCustomerWithOrder(customerRequest);

        // Assertions
        assertNotNull(result);
        assertEquals(customerRequest.getEmail(), result.getEmail());
        assertEquals(customerRequest.getPhoneNumber(), result.getPhoneNumber());
        assertNotNull(result.getOrders());
        assertEquals(2, result.getOrders().size());

        verify(customerRepository, times(1)).save(any());
        verify(orderedItemRepository, times(0)).saveAll(any());
    }
    @Test
    void getCustomerByIdReturnsException() {
        Customer customer = new Customer();
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(anyLong());
        });
    }
    @Test
    void testGetCustomerById() {
        // Mock customer and customer ID
        Customer customer = new Customer();
        Long customerId = 1L;
        customer.setCustomerId(customerId);

        // Mocking behavior of customerRepository.findById
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Calling the method to test
        Customer result = customerService.getCustomerById(customerId);

        // Assertions
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());

        // Verifying the behavior
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void deleteCustomerByIdReturnNothing() {

        // Mock the behavior of customerRepository
        //let max id is 10
        when(customerRepository.findMaxCustomerId()).thenReturn(10L);
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(new Customer()));

        // Calling the method to test
        customerService.deleteCustomerById(5L);
        verify(customerRepository).deleteById(5L);

    }

    @Test
    void updateCustomerReturnsCustomer() {
        // Create a mock CustomerRequest and an existing customer
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("Test");
        customerRequest.setEmail("test@abd.com");
        customerRequest.setPhoneNumber("1234567890");

        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerId(1L);
        existingCustomer.setName("Test2");
        existingCustomer.setEmail("test2@abc.com");
        existingCustomer.setPhoneNumber("9876543210");

        // Mocking the behavior of customerRepository
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Calling the method to test
        Customer updatedCustomer = customerService.updateCustomer(1L, customerRequest);

        // Assertions
        assertEquals("1234567890", updatedCustomer.getPhoneNumber());

        verify(customerRepository).save(any());
    }

    @Test
    void testUpdateCustomer_throwsCustomerNotFoundException() {
        when(customerRepository.save(any())).thenReturn(null);
        Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(1L, new CustomerRequest());
        });
    }

    @Test
    void findAllCustomerReturnCustomerList() {
        // Create some mock customers
        Customer customer1 = new Customer();
        customer1.setCustomerId(1L);
        customer1.setName("Customer 1");

        Customer customer2 = new Customer();
        customer2.setCustomerId(2L);
        customer2.setName("Customer 2");
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        List<Customer> allCustomerOrder = customerService.findAllCustomerOrder();
        assertNotNull(allCustomerOrder);
    }

    @Test
    void findAllCustomerThrowsException() {
        when(customerRepository.findAll()).thenThrow(CustomerNotFoundException.class);
        Assertions.assertThrows(CustomerNotFoundException.class, () -> {
            customerService.findAllCustomerOrder();
        });
    }

    @Test
     void testFindAllCustomerOrder() {
        // Create some mock customers
        Customer customer1 = new Customer();
        customer1.setCustomerId(1L);
        customer1.setName("Customer 1");

        Customer customer2 = new Customer();
        customer2.setCustomerId(2L);
        customer2.setName("Customer 2");

        // Create a mock Page
        Page<Customer> mockPage = new PageImpl<>(Arrays.asList(customer1, customer2));

        // Mock the behavior of customerRepository
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        // Call the service method
        Page<Customer> result = customerService.findAllCustomerOrder(PageRequest.of(0, 10));

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Customer 1", result.getContent().get(0).getName());
        assertEquals("Customer 2", result.getContent().get(1).getName());
    }

}