package com.snort.request;

import com.snort.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderedItemRequest {

    private String id;

    @NotBlank(message = "ProductName cannot be blank")
    private String productName;

    @Positive(message = "Quantity must be a positive number")
    private int quantity;

    @DecimalMin(value = "0.01", message = "Unit price must be greater than or equal to 0.01")
    private BigDecimal unitPrice;

//    private CustomerRequest customer;
}
