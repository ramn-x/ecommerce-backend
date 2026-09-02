package com.ecommerce.ecommerce_backend.DTO;

import com.ecommerce.ecommerce_backend.Entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusRequestDTO {

    @NotNull
    private OrderStatus status;
}