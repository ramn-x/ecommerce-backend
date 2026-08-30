package com.ecommerce.ecommerce_backend.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private double totalPrice;
    private LocalDateTime orderDate;

}