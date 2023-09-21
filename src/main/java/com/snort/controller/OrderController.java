package com.snort.controller;


import com.snort.entity.OrderedItem;
import com.snort.request.OrderedItemRequest;
import com.snort.service.CustomerService;
import com.snort.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;


    /**
     * @param customerId
     * @param orderId
     * @param orderedItem
     * @return updated order details
     */
    @PutMapping("/update/{customerId}/{orderId}")
    public ResponseEntity<OrderedItem> updateOrder(@PathVariable Long customerId, @PathVariable String orderId, @RequestBody OrderedItemRequest orderedItem){
        OrderedItem updatedOrder = orderService.updateOrderById(customerId, orderId, orderedItem);
        return new ResponseEntity<>(updatedOrder,HttpStatus.OK);
    }

    /**
     * @param customerId The ID of the customer
     * @param orderedItemRequest ordered details
     * @return OrderItem
     */
    @PostMapping("/{customerId}")
    public ResponseEntity<OrderedItem> addCustomerOrder(@PathVariable Long customerId, @RequestBody OrderedItemRequest orderedItemRequest){
        OrderedItem orderedItem = orderService.addOrderToCustomer(customerId, orderedItemRequest);
        return new ResponseEntity<>(orderedItem,HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderedItem> getOrderById(@PathVariable String orderId) {
        OrderedItem order = orderService.getOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    /**
     * @param id ordered item id.
     * @param customerId customer id
     */
    @DeleteMapping("/delete/{id}/{customerId}")
    public void deleteOrder(@PathVariable String id, @PathVariable Long customerId){
        orderService.deleteCustomerOrder(id,customerId);
    }
}