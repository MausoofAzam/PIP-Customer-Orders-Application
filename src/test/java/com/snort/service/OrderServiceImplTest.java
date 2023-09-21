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
//        given
        OrderedItemRequest updatedOrderedItemRequest = new OrderedItemRequest();
        updatedOrderedItemRequest.setProductName("Updated Product");
        updatedOrderedItemRequest.setQuantity(5);
        updatedOrderedItemRequest.setUnitPrice(BigDecimal.valueOf(15.0));

        String orderId = "1";

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        OrderedItem existingOrder = new OrderedItem();
        existingOrder.setId(orderId);
        existingOrder.setProductName("Original Product");
        existingOrder.setQuantity(3);
        existingOrder.setUnitPrice(BigDecimal.valueOf(10.0));
        List<OrderedItem> orders = new ArrayList<>();
        orders.add(existingOrder);
        customer.setOrders(orders);

//        when
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(orderedItemRepository.save(any())).thenReturn(new OrderedItem());

        OrderedItem result = orderService.updateOrderById(1L, orderId, updatedOrderedItemRequest);
//        then
        assertEquals(updatedOrderedItemRequest.getProductName(), result.getProductName());
        assertEquals(updatedOrderedItemRequest.getQuantity(), result.getQuantity());

        verify(orderedItemRepository, times(0)).save(any());
    }

    @Test
    void testUpdateOrderByIdCustomerNotFound() {
//        when
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
//          then
        assertThrows(CustomerNotFoundException.class, () -> orderService.updateOrderById(1L, "1", new OrderedItemRequest()));
    }

    @Test
    void testUpdateOrderByIdOrderNotFound() {
//      given
        Customer customer = new Customer();
        customer.setCustomerId(1L);
//       when
        when(customerRepository.findById(anyLong())).thenThrow(OrderNotFoundException.class);
//       then
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderById(1L, "1", new OrderedItemRequest()));
    }

    @Test
    void testAddOrderToCustomer() {
//        given
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setCustomerId(customerId);  // Set the customer ID
        customer.setOrders(new ArrayList<>());

        OrderedItemRequest orderedItemRequest = new OrderedItemRequest();
        orderedItemRequest.setProductName("Product1");
        orderedItemRequest.setQuantity(2);
        orderedItemRequest.setUnitPrice(BigDecimal.valueOf(10.0));
//        when
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        OrderedItem orderedItem = orderService.addOrderToCustomer(customerId, orderedItemRequest);

//        then
        assertNotNull(orderedItem);
        assertEquals(orderedItemRequest.getProductName(), orderedItem.getProductName());
        assertEquals(orderedItemRequest.getQuantity(), orderedItem.getQuantity());

        assertEquals(1, customer.getOrders().size());
        assertEquals(orderedItem, customer.getOrders().get(0));
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testGetOrderByIdOrderFound() {
        // given
        String orderId = "123";
        OrderedItem mockOrder = new OrderedItem();
        mockOrder.setId(orderId);
        //when
        when(orderedItemRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        OrderedItem result = orderService.getOrderById(orderId);

        // then
        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    void testGetOrderByIdOrderNotFound() {
        // given
        String orderId = "456";

        // when
        when(orderedItemRepository.findById(orderId)).thenReturn(Optional.empty());

        // then
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    void testDeleteCustomerOrder() {
//        given
        String orderId = "order123";
        Long customerId = 1L;
//        when
        orderService.deleteCustomerOrder(orderId, customerId);
//        then
        verify(orderedItemRepository, times(1)).deleteByIdAndCustomerId(orderId, customerId);
    }

    @Test
    void testDeleteCustomerOrderException() {
//        given
        String orderId = "order123";
        Long customerId = 1L;
//        when
        doThrow(new OrderDeleteException("an error occurred while deleting customer order")).when(orderedItemRepository).deleteByIdAndCustomerId(orderId, customerId);
        try {
            orderService.deleteCustomerOrder(orderId, customerId);
            fail("Expected OrderDeleteException but no exception was thrown");
        } catch (OrderDeleteException e) {
//            then
            assertEquals("an error occurred while deleting customer order", e.getMessage());
        }
    }
}