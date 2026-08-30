package com.ecommerce.ecommerce_backend.Controller;

import com.ecommerce.ecommerce_backend.DTO.LoginRequestDTO;
import com.ecommerce.ecommerce_backend.DTO.OrderDTO;
import com.ecommerce.ecommerce_backend.DTO.OrderRequestDTO;
import com.ecommerce.ecommerce_backend.DTO.UserDTO;
import com.ecommerce.ecommerce_backend.Service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/orders")
public class OrderController {


    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<OrderDTO> addOrder(
            @Valid @RequestBody
            OrderRequestDTO orderDTO){
        OrderDTO savedOrder = orderService.addOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
             .body(savedOrder);
    }
    @GetMapping
    public Page<OrderDTO> getAllOrder(Pageable pageable) {
        return orderService.getAllUser(pageable);
    }
    @GetMapping("/user/{userId}")
    public Page<OrderDTO> getOrdersByUserId(
            @PathVariable Integer userId,
            Pageable pageable) {

        return orderService.getOrdersByUserId(userId, pageable);
    }
    @GetMapping("/product/{productId}")
    public Page<OrderDTO> getOrdersByProductId(
            @PathVariable Integer productId, Pageable pageable) {

        return orderService.getOrdersByProductId(productId,pageable );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {

        orderService.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public OrderDTO updateOrder(
            @PathVariable Integer id,
            @Valid @RequestBody OrderRequestDTO  orderDTO) {

        return orderService.updateOrder(id, orderDTO);
    }


}
