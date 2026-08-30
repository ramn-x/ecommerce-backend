package com.ecommerce.ecommerce_backend.Exception;

import com.ecommerce.ecommerce_backend.Entity.Product;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String message){
        super(message);
    }
}
