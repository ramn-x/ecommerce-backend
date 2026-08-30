package com.ecommerce.ecommerce_backend.Exception;

public class InsufficientStockException extends  RuntimeException{

    public InsufficientStockException(String message){
        super(message);
    }
}
