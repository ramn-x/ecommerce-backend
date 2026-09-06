package com.ecommerce.ecommerce_backend.Exception;

public class InvalidCredentialsException extends  RuntimeException{
    public InvalidCredentialsException(String message){
        super(message);
    }
}
