package com.snort.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderedItem {
    @Id
    private String id;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore //preventing the infinite loop of serialization
    private Customer customer;

}
