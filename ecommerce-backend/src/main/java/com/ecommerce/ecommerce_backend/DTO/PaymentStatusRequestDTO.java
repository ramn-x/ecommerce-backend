package com.ecommerce.ecommerce_backend.DTO;

import com.ecommerce.ecommerce_backend.Entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentStatusRequestDTO {
     @NotNull
     private PaymentStatus status;
}
