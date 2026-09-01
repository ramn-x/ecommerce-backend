package com.ecommerce.ecommerce_backend.Controller;

import com.ecommerce.ecommerce_backend.DTO.CartDTO;
import com.ecommerce.ecommerce_backend.Service.CartService;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Validated
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartDTO> addToCart(
            @RequestParam Integer productid,
            @RequestParam Integer quantity) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        CartDTO cart = cartService.addToCart(
                productid,
                quantity,
                currentUserEmail
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cart);
    }
    @GetMapping
    public ResponseEntity<List<CartDTO>>getMyCart(){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail=
                authentication.getName();
        List<CartDTO> cart =
                cartService.getMyCart(currentUserEmail);
        return ResponseEntity.ok(cart);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CartDTO> updateQuantity(
            @PathVariable Integer id,
            @RequestParam @Min(1) Integer quantity) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        CartDTO updatedCart =
                cartService.updateQuantity(
                        id,
                        quantity,
                        currentUserEmail
                );

        return ResponseEntity.ok(updatedCart);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable Integer id) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        cartService.deleteCartItem(
                id,
                currentUserEmail
        );

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> getCartItem(
            @PathVariable Integer id) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        CartDTO cart =
                cartService.getCartItem(
                        id,
                        currentUserEmail
                );

        return ResponseEntity.ok(cart);
    }
}