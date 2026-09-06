package com.ecommerce.ecommerce_backend.Exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message){
        super(message);
    }
}
