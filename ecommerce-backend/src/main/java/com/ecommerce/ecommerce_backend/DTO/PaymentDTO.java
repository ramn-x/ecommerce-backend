package com.ecommerce.ecommerce_backend.DTO;

import com.ecommerce.ecommerce_backend.Entity.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Integer id;
    private Integer orderId;
    private double amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

}
