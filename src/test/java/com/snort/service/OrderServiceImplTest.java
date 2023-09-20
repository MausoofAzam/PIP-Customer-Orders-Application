package com.snort.service;

import com.snort.entity.Customer;
import com.snort.entity.OrderedItem;
import com.snort.exception.ex.CustomerNotFoundException;
import com.snort.exception.ex.OrderDeleteException;
import com.snort.exception.ex.OrderNotFoundException;
import com.snort.repository.CustomerRepository;
import com.snort.repository.OrderedItemRepository;
import com.snort.request.OrderedItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderedItemRepository orderedItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(orderedItemRepository, customerRepository);
    }

    @Test
    void testUpdateOrderById() {
        // Create a sample ordered item and order ID
        OrderedItem updatedOrderedItem = new OrderedItem();
        updatedOrderedItem.setProductName("Updated Product");
        updatedOrderedItem.setQuantity(5);
        updatedOrderedItem.setUnitPrice(BigDecimal.valueOf(15.0));

        String orderId = "1";  // Sample order ID

        // Create a sample customer with orders
        Customer customer = new Customer();
        customer.setCustomerId(1L);  // Sample customer ID
        OrderedItem existingOrder = new OrderedItem();
        existingOrder.setId(orderId);
        existingOrder.setProductName("Original Product");
        existingOrder.setQuantity(3);
        existingOrder.setUnitPrice(BigDecimal.valueOf(10.0));
        List<OrderedItem> orders = new ArrayList<>();
        orders.add(existingOrder);
        customer.setOrders(orders);

        // Mock repository behavior
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(orderedItemRepository.save(any())).thenReturn(updatedOrderedItem);

        // Invoke the service method to update the order
        OrderedItem result = orderService.updateOrderById(1L, orderId, updatedOrderedItem);

        // Verify that the order was updated
        assertEquals(updatedOrderedItem.getProductName(), result.getProductName());
        assertEquals(updatedOrderedItem.getQuantity(), result.getQuantity());
        assertEquals(updatedOrderedItem.getUnitPrice(), result.getUnitPrice());

        // Verify that the repository methods were called
        verify(customerRepository, times(1)).findById(anyLong());
        verify(orderedItemRepository, times(0)).save(any());
    }

    @Test
    void testUpdateOrderByIdCustomerNotFound() {
        // Mock repository behavior to return an empty optional (customer not found)
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Invoke the service method and expect a CustomerNotFoundException
        assertThrows(CustomerNotFoundException.class,
                () -> orderService.updateOrderById(1L, "1", new OrderedItem()));
    }

    @Test
    void testUpdateOrderByIdOrderNotFound() {
        // Create a sample customer with no orders
        Customer customer = new Customer();
        customer.setCustomerId(1L);  // Sample customer ID

        // Mock repository behavior
        when(customerRepository.findById(anyLong())).thenThrow(OrderNotFoundException.class);

        // Invoke the service method and expect an OrderNotFoundException
        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrderById(1L, "1", new OrderedItem()));
    }

    @Test
     void testAddOrderToCustomer() {
        // Mock customer data
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setCustomerId(customerId);  // Set the customer ID
        customer.setOrders(new ArrayList<>());

        // Mock ordered item request
        OrderedItemRequest orderedItemRequest = new OrderedItemRequest();
        orderedItemRequest.setProductName("Product1");
        orderedItemRequest.setQuantity(2);
        orderedItemRequest.setUnitPrice(BigDecimal.valueOf(10.0));

        // Mock the behavior of the customerRepository
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Call the service method
        OrderedItem orderedItem = orderService.addOrderToCustomer(customerId, orderedItemRequest);

        // Verify that the order was added to the customer and saved to the repository
        assertNotNull(orderedItem);
        assertEquals(orderedItemRequest.getProductName(), orderedItem.getProductName());
        assertEquals(orderedItemRequest.getQuantity(), orderedItem.getQuantity());
//        assertEquals(orderedItemRequest.getUnitPrice(), orderedItem.getUnitPrice(), 0.01);

        // Verify that the customer's order list was updated and saved to the repository
        assertEquals(1, customer.getOrders().size());
        assertEquals(orderedItem, customer.getOrders().get(0));
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testDeleteCustomerOrder() {
        // Mock data
        String orderId = "order123";
        Long customerId = 1L;

        // Call the service method
        orderService.deleteCustomerOrder(orderId, customerId);

        // Verify that the orderedItemRepository's deleteByIdAndCustomerId was called with the correct parameters
        verify(orderedItemRepository, times(1)).deleteByIdAndCustomerId(orderId, customerId);
    }

    @Test
    void testDeleteCustomerOrderException() {
        // Mock data
        String orderId = "order123";
        Long customerId = 1L;

        // Mock the behavior of the orderedItemRepository to throw an exception
        doThrow(new OrderDeleteException("an error occurred while deleting customer order")).when(orderedItemRepository).deleteByIdAndCustomerId(orderId, customerId);

        try {
            // Call the service method which may throw an exception
            orderService.deleteCustomerOrder(orderId, customerId);

            // If the method doesn't throw an exception, fail the test
            fail("Expected OrderDeleteException but no exception was thrown");
        } catch (OrderDeleteException e) {
            // Assert on the exception message or other details if needed
            assertEquals("an error occurred while deleting customer order", e.getMessage());
        }
    }
}