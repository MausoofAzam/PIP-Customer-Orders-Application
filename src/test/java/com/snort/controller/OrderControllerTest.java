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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        OrderedItem orderedItem = new OrderedItem();
        orderedItem.setProductName("test");
        OrderedItem updatedOrderItem = new OrderedItem();
        updatedOrderItem.setId(orderId);
        updatedOrderItem.setProductName("test1");
//        when
        when(orderService.updateOrderById(anyLong(),anyString(),any(OrderedItem.class))).thenReturn(updatedOrderItem);
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