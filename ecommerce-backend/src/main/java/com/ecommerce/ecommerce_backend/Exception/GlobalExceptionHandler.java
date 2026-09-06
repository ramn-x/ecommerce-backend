package com.ecommerce.ecommerce_backend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String,String>>handleProductNotFoundException(ProductNotFoundException ex){
        Map<String,String>error= new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status","404");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

//  validation exceptiom
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>>handleValidationException(
            MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

//    General exception
//    Now we'll handle unexpected errors that aren't specifically handled by our other exceptions
@ExceptionHandler(Exception.class)
public ResponseEntity<?> handleGeneralException(Exception ex) {

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Something went wrong");
}

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,String>>handleUserNotFoundException(UserNotFoundException ex){
        Map<String,String> error= new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status","404");
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String,String>>
            handleOrderNotFoundException(OrderNotFoundException ex){
        Map<String,String>error= new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status","404");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientStockException(
            InsufficientStockException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", "400");

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String ,String>>
         handleEmailAlreadyExistsException
            (EmailAlreadyExistsException ex){
        Map<String,String> error =new HashMap<>();
        error.put("message",ex.getMessage());
        error.put("status","something wrong");
        return new ResponseEntity<>(error,HttpStatus.CONFLICT);

    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Access denied");
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<String> handleInvalidOrderStatus(
            InvalidOrderStatusException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(
            IllegalStateException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePaymentNotFoundException(
            PaymentNotFoundException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", "404");

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(
            InvalidCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFoundException(
            CartNotFoundException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", "404");

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
