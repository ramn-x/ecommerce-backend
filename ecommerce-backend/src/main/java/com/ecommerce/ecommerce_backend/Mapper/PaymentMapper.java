package com.ecommerce.ecommerce_backend.Mapper;

import com.ecommerce.ecommerce_backend.DTO.PaymentDTO;
import com.ecommerce.ecommerce_backend.Entity.Payment;

public class PaymentMapper {
    public static PaymentDTO toDTO(Payment payment){
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }
    public static Payment toEntity(PaymentDTO dto){
        Payment payment =new Payment();

        payment.setId(dto.getId());
        payment.setOrderId(dto.getOrderId());
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus());
        payment.setPaymentDate(dto.getPaymentDate());
        return payment;
    }

}
