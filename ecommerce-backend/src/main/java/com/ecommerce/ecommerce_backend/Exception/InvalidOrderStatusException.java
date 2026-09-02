package com.ecommerce.ecommerce_backend.Exception;

public class InvalidOrderStatusException extends RuntimeException{

    public  InvalidOrderStatusException(String message){
        super(message);
    }
}
