package com.snort.service;

import com.snort.entity.Customer;
import com.snort.entity.OrderedItem;
import com.snort.exception.ex.CustomerNotFoundException;
import com.snort.exception.ex.OrderDeleteException;
import com.snort.exception.ex.OrderNotFoundException;
import com.snort.repository.CustomerRepository;
import com.snort.repository.OrderedItemRepository;
import com.snort.request.OrderedItemRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.snort.enums.Constants.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderedItemRepository orderedItemRepository;
    private final CustomerRepository customerRepository;

    public OrderServiceImpl(OrderedItemRepository orderedItemRepository, CustomerRepository customerRepository) {
        this.orderedItemRepository = orderedItemRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * @param customerId  id of the customer
     * @param orderId     id of the order
     * @param orderedItemRequest details of the order item
     * @return OrderedItem details of the order entity
     * @throws CustomerNotFoundException it happens when customer not found in DB.
     * @throws OrderNotFoundException    it occurs when order not found in the DB.
     */
    @Override
    public OrderedItem updateOrderById(Long customerId, String orderId, OrderedItemRequest orderedItemRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            log.info("Customer with ID not found in DB: {}", customerId);
            throw new CustomerNotFoundException(CUSTOMER_NOT_FOUND.getValue() + customerId);
        }

        Customer existingCustomer = optionalCustomer.get();

        OrderedItem orderToUpdate = null;
        for (OrderedItem order : existingCustomer.getOrders()) {
            if (order.getId().equals(orderId)) {
                orderToUpdate = order;
                break;
            }
        }

        if (orderToUpdate == null) {
            log.info("Order with ID not found in the DB: {}", orderId);
            throw new OrderNotFoundException(ORDER_NOT_FOUND.getValue() + orderId);
        }

        orderToUpdate.setProductName(orderedItemRequest.getProductName());
        orderToUpdate.setQuantity(orderedItemRequest.getQuantity());
        orderToUpdate.setUnitPrice(orderedItemRequest.getUnitPrice());

        log.info("Order with ID {} for Customer {} updated successfully", orderId, customerId);
        customerRepository.save(existingCustomer);
        return orderToUpdate;
    }

    /**
     * @param customerId         customer id
     * @param orderedItemRequest order details request
     * @return OrderedItem returns orderItem entity details
     * @throws CustomerNotFoundException it occurs when customer not found in the DB.
     */
    @Override
    public OrderedItem addOrderToCustomer(Long customerId, OrderedItemRequest orderedItemRequest) {
       log.info("accepting request payload with customerId {} orderItemRequest {}",customerId,orderedItemRequest);
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            //creating an order entity from the orderRequest
            OrderedItem orderedItem = new OrderedItem();
            orderedItem.setId(UUID.randomUUID().toString());
            orderedItem.setProductName(orderedItemRequest.getProductName());
            orderedItem.setQuantity(orderedItemRequest.getQuantity());
            orderedItem.setUnitPrice(orderedItemRequest.getUnitPrice());

            //setting the customer for the order
            orderedItem.setCustomer(customer);
            //adding  the order to customer list of orders
            customer.getOrders().add(orderedItem);

            customerRepository.save(customer);
            return orderedItem;
        } else {
            log.error("error occurred while creating OrderItems ");
            throw new CustomerNotFoundException(CUSTOMER_NOT_FOUND.getValue() + customerId);
        }
    }
    @Override
    public OrderedItem getOrderById(String orderId) {
        Optional<OrderedItem> optionalOrder = orderedItemRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            return optionalOrder.get();
        } else {
            throw new OrderNotFoundException(ORDER_NOT_FOUND.getValue() + orderId);
        }
    }

    /**
     * @param id  order id
     * @param customerId customer id
     * @throws OrderDeleteException if the order not deleted then it will throw this exception
     */
    @Override
    public void deleteCustomerOrder(String id, Long customerId) {
        try {
            orderedItemRepository.deleteByIdAndCustomerId(id, customerId);
        } catch (Exception e) {
            log.error("error occurred while deleting the Order : {}",id);
            throw new OrderDeleteException(ERROR_OCCURRED.getValue());
        }
    }
}