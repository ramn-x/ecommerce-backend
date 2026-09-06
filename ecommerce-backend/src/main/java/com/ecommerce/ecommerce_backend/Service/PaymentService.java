package com.ecommerce.ecommerce_backend.Service;

import com.ecommerce.ecommerce_backend.DTO.PaymentDTO;
import com.ecommerce.ecommerce_backend.DTO.PaymentStatusRequestDTO;
import com.ecommerce.ecommerce_backend.Entity.*;
import com.ecommerce.ecommerce_backend.Exception.*;
import com.ecommerce.ecommerce_backend.Mapper.PaymentMapper;
import com.ecommerce.ecommerce_backend.Repository.OrderRepository;
import com.ecommerce.ecommerce_backend.Repository.PaymentRepository;
import com.ecommerce.ecommerce_backend.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
                        new PaymentNotFoundException (
                                "Payment not found with id: " + id));

        // Find order
        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: "
                                        + payment.getOrderId()));

        // Find logged-in user
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: "
                                        + currentUserEmail));

        // Ownership check
        if (!order.getUserId().equals(currentUser.getId())
                && !currentUser.getRole().equals("ADMIN")) {

            throw new AccessDeniedException("Access denied");
        }

        PaymentStatus newStatus = statusDTO.getStatus();
        PaymentStatus currentStatus = payment.getStatus();

        // Payment status validation
        if (currentStatus == PaymentStatus.PENDING
                && (newStatus == PaymentStatus.PAID
                || newStatus == PaymentStatus.FAILED)) {

            payment.setStatus(newStatus);

        } else if (currentStatus == PaymentStatus.PAID
                && newStatus == PaymentStatus.REFUNDED) {

            payment.setStatus(newStatus);

        } else {
            throw new IllegalStateException(
                    "Invalid payment status transition from "
                            + currentStatus + " to " + newStatus);
        }

        Payment updatedPayment = paymentRepository.save(payment);

        // PAID → Order CONFIRMED
        if (newStatus == PaymentStatus.PAID) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }

        return PaymentMapper.toDTO(updatedPayment);
    }


    public PaymentDTO getPaymentById(Integer id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                 new ProductNotFoundException(
                 "Payment not found with id: " + id));

        return PaymentMapper.toDTO(payment);
    }
    public PaymentDTO getPaymentByOrderId(Integer orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found for order: " + orderId));

        return PaymentMapper.toDTO(payment);
    }


    public List<PaymentDTO> getPayments(String currentUserEmail) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: " + currentUserEmail));

        List<Payment> payments = paymentRepository.findAll();

        if (currentUser.getRole().equals("ADMIN")) {
            return payments.stream()
                    .map(PaymentMapper::toDTO)
                    .toList();
        }

        return payments.stream()
                .filter(payment -> {
                    Order order = orderRepository.findById(payment.getOrderId())
                            .orElse(null);

                    return order != null
                            && order.getUserId().equals(currentUser.getId());
                })
                .map(PaymentMapper::toDTO)
                .toList();
    }
}
