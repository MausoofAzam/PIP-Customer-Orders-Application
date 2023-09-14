package com.snort.service;
import com.snort.entity.OrderedItem;
import com.snort.request.OrderedItemRequest;


public interface OrderService {

    OrderedItem updateOrderById(Long customerId, String orderId, OrderedItem orderedItem);
    OrderedItem addOrderToCustomer(Long customerId, OrderedItemRequest orderedItemRequest);

    void deleteCustomerOrder(String id, Long customerId);
}
