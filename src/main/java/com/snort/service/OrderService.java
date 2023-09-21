package com.snort.service;
import com.snort.entity.OrderedItem;
import com.snort.request.OrderedItemRequest;


public interface OrderService {

    OrderedItem updateOrderById(Long customerId, String orderId, OrderedItemRequest orderedItem);
    OrderedItem addOrderToCustomer(Long customerId, OrderedItemRequest orderedItemRequest);
     OrderedItem getOrderById(String orderId);
    void deleteCustomerOrder(String id, Long customerId);
}
