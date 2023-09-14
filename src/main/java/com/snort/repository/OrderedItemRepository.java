package com.snort.repository;

import com.snort.entity.OrderedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderedItemRepository extends JpaRepository<OrderedItem, String > {

    @Transactional
    @Modifying
    @Query(value = "delete from ordered_item where id = :id and customer_id= :customerId",nativeQuery = true)
    void deleteByIdAndCustomerId(String id, Long customerId);
}
