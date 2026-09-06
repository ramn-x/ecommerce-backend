package com.ecommerce.ecommerce_backend.Controller;

import com.ecommerce.ecommerce_backend.DTO.OrderDTO;
import com.ecommerce.ecommerce_backend.DTO.OrderRequestDTO;
import com.ecommerce.ecommerce_backend.DTO.OrderStatusRequestDTO;
import com.ecommerce.ecommerce_backend.Service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<OrderDTO> addOrder(
            @Valid @RequestBody OrderRequestDTO orderDTO) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        OrderDTO savedOrder =
                orderService.addOrder(
                        orderDTO,
                        currentUserEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedOrder);
    }
    @GetMapping
    public Page<OrderDTO> getAllOrder(Pageable pageable) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();
        return orderService.getAllOrders(
                currentUserEmail,
                pageable
        );
    }

    @GetMapping("/user/{userId}")
    public Page<OrderDTO> getOrdersByUserId(
            @PathVariable Integer userId,
            Pageable pageable) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        return orderService.getOrdersByUserId(
                userId,
                currentUserEmail,
                pageable
        );
    }

    @GetMapping("/product/{productId}")
    public Page<OrderDTO> getOrdersByProductId(
            @PathVariable Integer productId,
            Pageable pageable) {

        return orderService.getOrdersByProductId(
                productId,
                pageable
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Integer id) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext().getAuthentication();

        String currentUserEmail =
                authentication.getName();

        orderService.deleteOrder(id,
                currentUserEmail);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PutMapping("/{id}")
    public OrderDTO updateOrder(
            @PathVariable Integer id,
            @Valid @RequestBody OrderRequestDTO orderDTO) {

        return orderService.updateOrder(id, orderDTO);
    }

    @PostMapping("/checkout")
    public ResponseEntity<List<OrderDTO>> checkout() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        List<OrderDTO> orders =
                orderService.checkout(currentUserEmail);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orders);
    }

    @PutMapping("{id}/status")
    public OrderDTO updateOrderStatus (
            @PathVariable Integer id,
            @Valid @RequestBody OrderStatusRequestDTO statusDTO ){
        return orderService.updateOrderStatus(id,statusDTO);

    }
    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Integer id) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail = authentication.getName();

        return orderService.getOrderById(
                id,
                currentUserEmail
        );
    }
}