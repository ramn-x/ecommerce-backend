package com.ecommerce.ecommerce_backend.Exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message){
        super(message);
    }
}
