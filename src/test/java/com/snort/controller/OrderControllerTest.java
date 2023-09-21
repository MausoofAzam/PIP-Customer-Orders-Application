package com.snort.controller;

import com.snort.entity.OrderedItem;
import com.snort.request.OrderedItemRequest;
import com.snort.service.CustomerService;
import com.snort.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class OrderControllerTest {
    @Mock
    private OrderService orderService;
    @Mock
    private CustomerService customerService;
    @InjectMocks
    private OrderController orderController;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void updateOrder_shouldUpdateOrder(){
//        given
        long customerId=101L;
        String orderId ="101";
        OrderedItemRequest orderedItem = new OrderedItemRequest();
        orderedItem.setProductName("test");
        OrderedItem updatedOrderItem = new OrderedItem();
        updatedOrderItem.setId(orderId);
        updatedOrderItem.setProductName("test1");
//        when
        when(orderService.updateOrderById(anyLong(),anyString(),any(OrderedItemRequest.class))).thenReturn(updatedOrderItem);
        ResponseEntity<OrderedItem> responseEntity= orderController.updateOrder(customerId,orderId,orderedItem);
//        then
        Assertions.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        Assertions.assertEquals(updatedOrderItem,responseEntity.getBody());
    }
    @Test
    void addCustomerOrder_shouldAddOrder(){
//        given
        long customerId = 1L;
        OrderedItemRequest orderedItemRequest = new OrderedItemRequest();
        orderedItemRequest.setProductName("test");
        OrderedItem addedOrder = new OrderedItem();
        addedOrder.setId("1");
        addedOrder.setProductName("test");
//        when
        when(orderService.addOrderToCustomer(anyLong(),any(OrderedItemRequest.class))).thenReturn(addedOrder);
        ResponseEntity<OrderedItem> responseEntity = orderController.addCustomerOrder(customerId, orderedItemRequest);
        //then
        Assertions.assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        Assertions.assertEquals(addedOrder,responseEntity.getBody());
    }
    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists() {
        // Given
        String orderId = "123";
        OrderedItem expectedOrder = new OrderedItem();
        expectedOrder.setId(orderId);
        given(orderService.getOrderById(orderId)).willReturn(expectedOrder);

        // When
        ResponseEntity<OrderedItem> responseEntity = orderController.getOrderById(orderId);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedOrder);
        verify(orderService, times(1)).getOrderById(orderId);
    }
    @Test
    void deleteOrder_shouldDeleteOrder(){
//        given
        long customerId =101L;
        String orderId = "101";
//        when
        orderController.deleteOrder(orderId,customerId);
//        then
        verify(orderService).deleteCustomerOrder(orderId,customerId);
    }
}