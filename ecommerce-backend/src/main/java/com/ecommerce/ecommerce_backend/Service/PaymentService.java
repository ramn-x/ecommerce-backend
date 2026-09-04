package com.ecommerce.ecommerce_backend.Service;

import com.ecommerce.ecommerce_backend.DTO.PaymentDTO;
import com.ecommerce.ecommerce_backend.DTO.PaymentStatusRequestDTO;
import com.ecommerce.ecommerce_backend.Entity.*;
import com.ecommerce.ecommerce_backend.Exception.AccessDeniedException;
import com.ecommerce.ecommerce_backend.Exception.OrderNotFoundException;
import com.ecommerce.ecommerce_backend.Exception.UserNotFoundException;
import com.ecommerce.ecommerce_backend.Mapper.PaymentMapper;
import com.ecommerce.ecommerce_backend.Repository.OrderRepository;
import com.ecommerce.ecommerce_backend.Repository.PaymentRepository;
import com.ecommerce.ecommerce_backend.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public PaymentDTO createPayment(
            Integer orderId,
            String currentUserEmail) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + orderId));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: "
                                        + currentUserEmail));

        if (!order.getUserId().equals(currentUser.getId())
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }
        if (!paymentRepository.findByOrderId(orderId).isEmpty()) {
            throw new IllegalStateException(
                    "Payment already exists for order: " + orderId);
        }

        Payment payment = new Payment();

        payment.setOrderId(order.getId());
        payment.setAmount(order.getTotalPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentMapper.toDTO(savedPayment);
    }

    public PaymentDTO updatePaymentStatus(
            Integer id,
            PaymentStatusRequestDTO statusDTO,
            String currentUserEmail) {


            Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Payment not found with id: " + id));

            PaymentStatus newStatus = statusDTO.getStatus();

            if (payment.getStatus() != PaymentStatus.PENDING) {
                throw new IllegalStateException(
                        "Payment status cannot be changed from "
                                + payment.getStatus());
            }

            payment.setStatus(newStatus);

            Payment updatedPayment = paymentRepository.save(payment);
            if (newStatus == PaymentStatus.PAID) {
                Order order = orderRepository
                        .findById(payment.getOrderId())
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "order not found with id" +
                                                payment.getOrderId()));
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
            }
            return PaymentMapper.toDTO(updatedPayment);
        }
    }
