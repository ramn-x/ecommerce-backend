package com.ecommerce.ecommerce_backend.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderRequestDTO {


    @NotNull
    @Positive
    private Integer userId;

    @NotNull
    @Positive
    private Integer productId;

    @NotNull
    @Positive
    private Integer quantity;
}
