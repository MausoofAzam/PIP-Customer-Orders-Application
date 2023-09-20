package com.snort.repository;

import com.snort.entity.Customer;
import com.snort.request.CustomerRequest;
import com.snort.service.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void testExistsByEmail() {
        String email = "azam@xyz.com";
        when(customerRepository.existsByEmail(email)).thenReturn(true);

        boolean result = customerRepository.existsByEmail(email);

        assertThat(result).isTrue();
    }

    @Test
    void testExistsByPhoneNumber() {
        String phoneNumber = "1234567890";
        when(customerRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);
        boolean result = customerRepository.existsByPhoneNumber(phoneNumber);

        assertThat(result).isTrue();
    }

    @Test
    void testFindMaxCustomerId() {
        Long maxCustomerId = 42L;
        when(customerRepository.findMaxCustomerId()).thenReturn(maxCustomerId);
        Long result = customerRepository.findMaxCustomerId();
        assertThat(result).isEqualTo(maxCustomerId);
    }

    @Test
    void testSaveCustomer() {
        Customer customer = new Customer();
        customer.setName("Test");

        when(customerRepository.save(customer)).thenReturn(customer);

        Customer savedCustomer = customerRepository.save(customer);

        assertNotNull(savedCustomer);
        assertEquals("Test", savedCustomer.getName());
        verify(customerRepository).save(customer);
    }

    @Test
    void testFindAllCustomerOrder() {
        Customer customer1 = new Customer();
        customer1.setCustomerId(1L);
        customer1.setName("Customer 1");

        Customer customer2 = new Customer();
        customer2.setCustomerId(2L);
        customer2.setName("Customer 2");

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> result = customerService.findAllCustomerOrder();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Customer 1", result.get(0).getName());
        assertEquals("Customer 2", result.get(1).getName());

        verify(customerRepository).findAll();
    }

    @Test
    void testFindAllCustomerOrderWithPageable() {
        Customer customer1 = new Customer();
        customer1.setCustomerId(1L);
        customer1.setName("Customer 1");

        Customer customer2 = new Customer();
        customer2.setCustomerId(2L);
        customer2.setName("Customer 2");

        Page<Customer> mockPage = new PageImpl<>(Arrays.asList(customer1, customer2));
        when(customerRepository.findAll(Mockito.any(Pageable.class))).thenReturn(mockPage);

        Page<Customer> result = customerService.findAllCustomerOrder(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Customer 1", result.getContent().get(0).getName());
        assertEquals("Customer 2", result.getContent().get(1).getName());

        verify(customerRepository).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void testGetCustomerById() {
        Customer sampleCustomer = new Customer();
        sampleCustomer.setCustomerId(1L);
        sampleCustomer.setName("John Doe");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        Customer result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("John Doe", result.getName());

        verify(customerRepository).findById(1L);
    }

    @Test
    void testDeleteCustomerById() {
        Long customerIdToDelete = 1L;
        Long nextCustomerId = 2L;

        when(customerRepository.findMaxCustomerId()).thenReturn(nextCustomerId);
        when(customerRepository.findById(nextCustomerId)).thenReturn(Optional.of(new Customer()));

        customerService.deleteCustomerById(customerIdToDelete);

        verify(customerRepository).deleteById(customerIdToDelete);
        verify(customerRepository).findById(nextCustomerId);
        verify(customerRepository).save(Mockito.any());
    }

    @Test
    void testUpdateCustomer() {
        Long customerIdToUpdate = 1L;
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("New Name");
        customerRequest.setEmail("new@example.com");
        customerRequest.setPhoneNumber("1234567890");

        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerId(customerIdToUpdate);

        when(customerRepository.findById(customerIdToUpdate)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updatedCustomer = customerService.updateCustomer(customerIdToUpdate, customerRequest);

        verify(customerRepository).save(existingCustomer);
    }
}
