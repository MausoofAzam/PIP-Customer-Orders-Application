package com.snort.controller;

import com.snort.entity.Customer;
import com.snort.request.CustomerRequest;
import com.snort.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
class CustomerControllerTest {
    @Mock
    private CustomerService customerService;
    @InjectMocks
    private CustomerController customerController;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void createCustomerWithOrder_ReturnsCreatingCustomer() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("Mausoof");
        customerRequest.setEmail("azam@xyz.com");
        customerRequest.setPhoneNumber("9876589301");
        customerRequest.setAddress("Mumbai");
        Customer createdCustomer = new Customer();
//        given
        given(customerService.createCustomerWithOrder(customerRequest)).willReturn(createdCustomer);
//        when
        ResponseEntity<?> customerWithOrder = customerController.createCustomerWithOrder(customerRequest);
//        then
        BDDMockito.then(customerService).should().createCustomerWithOrder(customerRequest);
        Assertions.assertEquals(HttpStatus.CREATED, customerWithOrder.getStatusCode());
    }
    @Test
    void createCustomerWithOrder_shouldReturnBadRequestIfRequestBodyIsNull() {
        //given
        CustomerRequest customerRequest = null;
        //when
        ResponseEntity<?> customerWithOrder = customerController.createCustomerWithOrder(customerRequest);
        //then
        BDDMockito.then(customerService).should(never()).createCustomerWithOrder(isNull());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, customerWithOrder.getStatusCode());
        Assertions.assertEquals("RequestBody is missing", customerWithOrder.getBody());
    }
    @Test
    void getCustomerById_shouldReturnCustomer() {
//        given
        Long customerId = 1L;
        Customer expectedCustomer = new Customer();
        given(customerService.getCustomerById(customerId)).willReturn(expectedCustomer);
//        when
        ResponseEntity<Customer> responseEntity = customerController.getCustomerDetailsById(customerId);
//         then
        Assertions.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        Assertions.assertEquals(expectedCustomer,responseEntity.getBody());
    }
    private List<Customer> createCustomerOrderList(){
        Customer customer1 = new Customer();
        Customer customer2 = new Customer();
        List<Customer> customerList = Arrays.asList(customer1, customer2);
        return customerList;
    }
    @Test
    void findAllCustomerOrders_shouldReturnCustomerOrderList(){
//        given
        List<Customer> expectedCustomerList = createCustomerOrderList();
        given(customerService.findAllCustomerOrder()).willReturn(expectedCustomerList);
//        when
        ResponseEntity<List<Customer>> responseEntity = customerController.findAllCustomerOrders();
//        then
        Assertions.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        Assertions.assertEquals(expectedCustomerList,responseEntity.getBody());
    }
    @Test
    void getAllCustomer_shouldReturnCustomerPage(){
//        given
        int page = 0;
        int size = 10;
//        when
        Page<Customer> customerPage = mock(Page.class);
        when(customerService.findAllCustomerOrder(any(PageRequest.class))).thenReturn(customerPage);
        Page<Customer> result = customerController.getAllCustomer(page, size);
//        then
        verify(customerService).findAllCustomerOrder(PageRequest.of(page,size));
        Assertions.assertEquals(customerPage,result);
    }
    @Test
    void deleteCustomerWithOrder_shouldDeleteCustomer(){
//        given
        long customerId =101L;
//        when
        ResponseEntity<String > responseEntity = customerController.deleteCustomerWithOrder(customerId);
//        then
        verify(customerService).deleteCustomerById(customerId);
        Assertions.assertEquals(HttpStatus.NO_CONTENT,responseEntity.getStatusCode());
    }
    @Test
    void updateCustomer_shouldUpdateCustomer(){
//        given
        Long customerId = 101L;
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("unit test");
        customerRequest.setEmail("test@xyz.com");
        customerRequest.setAddress("chennai");
        Customer updatedCustomer = new Customer();
        updatedCustomer.setCustomerId(customerId);
        updatedCustomer.setName("test1");
//        when
        when(customerService.updateCustomer(anyLong(),any(CustomerRequest.class))).thenReturn(updatedCustomer);
        ResponseEntity<Customer> responseEntity = customerController.updateCustomer(customerId, customerRequest);
//        then
        verify(customerService).updateCustomer(customerId,customerRequest);
        Assertions.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        Assertions.assertEquals(updatedCustomer,responseEntity.getBody());
    }
}
