package com.ecommerce.ecommerce_backend.Service;

import com.ecommerce.ecommerce_backend.DTO.OrderDTO;
import com.ecommerce.ecommerce_backend.DTO.OrderRequestDTO;
import com.ecommerce.ecommerce_backend.Entity.Cart;
import com.ecommerce.ecommerce_backend.Entity.Order;
import com.ecommerce.ecommerce_backend.Entity.Product;
import com.ecommerce.ecommerce_backend.Entity.User;
import com.ecommerce.ecommerce_backend.Exception.*;
import com.ecommerce.ecommerce_backend.Mapper.OrderMapper;
import com.ecommerce.ecommerce_backend.Repository.CartRepository;
import com.ecommerce.ecommerce_backend.Repository.OrderRepository;
import com.ecommerce.ecommerce_backend.Repository.ProductRepository;
import com.ecommerce.ecommerce_backend.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository, CartRepository cartRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    // Create Order
    @Transactional
    public OrderDTO addOrder(
            OrderRequestDTO orderDTO,
            String currentUserEmail) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Current user not found with email: "
                                        + currentUserEmail));

        if (!currentUser.getId().equals(orderDTO.getUserId())
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }

        userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: "
                                        + orderDTO.getUserId()));

        Product product =
                productRepository.findById(orderDTO.getProductId())
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + orderDTO.getProductId()));

        if (product.getQuantity() < orderDTO.getQuantity()) {
            throw new InsufficientStockException("Not enough stock");
        }

        double totalPrice =
                product.getPrice() * orderDTO.getQuantity();

        product.setQuantity(
                product.getQuantity() - orderDTO.getQuantity());

        productRepository.save(product);

        Order order = OrderMapper.toEntity(orderDTO);

        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        return OrderMapper.toDTO(savedOrder);
    }

    // Get All Orders
    public Page<OrderDTO> getAllUser(Pageable pageable) {

        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(OrderMapper::toDTO);
    }
    // Get Order By ID
    public Page<OrderDTO> getOrdersByUserId(
            Integer userId,
            String currentUserEmail,
            Pageable pageable) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Current user not found with email: "
                                        + currentUserEmail));

        if (!currentUser.getId().equals(userId)
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }

        Page<Order> orders =
                orderRepository.findByUserId(userId, pageable);

        return orders.map(OrderMapper::toDTO);
    }
    public Page <OrderDTO> getOrdersByProductId(Integer productId ,Pageable pageable) {

        Page<Order> orders = orderRepository.findByProductId(productId,pageable);

        return orders
                .map(OrderMapper::toDTO);
    }
    // Delete Order
    public void deleteOrder(Integer id) {
      Order order = orderRepository.findById(id)
              .orElseThrow(()->
                 new OrderNotFoundException(
                       "Order not found with id: " + id));
      orderRepository.delete(order);
    }
    // update Order
    @Transactional
    public OrderDTO updateOrder(Integer id, OrderRequestDTO  orderDTO) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id));
        Integer oldQuantity = existingOrder.getQuantity();


        Product product = productRepository.findById(orderDTO.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: "
                                        + orderDTO.getProductId()));
        Integer newQuantity = orderDTO.getQuantity();
        Integer quantityDifference = newQuantity - oldQuantity;
        if (quantityDifference > 0 &&
                product.getQuantity() < quantityDifference) {

            throw new InsufficientStockException("Not enough stock");
        }
        product.setQuantity(product.getQuantity() - quantityDifference);
        productRepository.save(product);

        double totalPrice =
                product.getPrice() * orderDTO.getQuantity();

        existingOrder.setUserId(orderDTO.getUserId());
        existingOrder.setProductId(orderDTO.getProductId());
        existingOrder.setQuantity(orderDTO.getQuantity());
        existingOrder.setTotalPrice(totalPrice);

        Order updatedOrder = orderRepository.save(existingOrder);

        return OrderMapper.toDTO(updatedOrder);
    }
}
