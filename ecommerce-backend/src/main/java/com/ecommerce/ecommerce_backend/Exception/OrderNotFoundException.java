package com.ecommerce.ecommerce_backend.Exception;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(String message){
        super(message);
    }

}
