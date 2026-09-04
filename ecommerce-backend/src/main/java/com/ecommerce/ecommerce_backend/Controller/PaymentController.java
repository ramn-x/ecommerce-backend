package com.ecommerce.ecommerce_backend.Controller;

import com.ecommerce.ecommerce_backend.DTO.PaymentDTO;
import com.ecommerce.ecommerce_backend.DTO.PaymentStatusRequestDTO;
import com.ecommerce.ecommerce_backend.Service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    public final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentDTO> createPayment(
            @PathVariable Integer orderId) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail = authentication.getName();

        PaymentDTO payment =
                paymentService.createPayment(
                        orderId,
                        currentUserEmail);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payment);
    }
    @PutMapping("/{id}/status")
    public PaymentDTO updatePaymentStatus(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentStatusRequestDTO statusDTO) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String currentUserEmail = authentication.getName();

        return paymentService.updatePaymentStatus(
                id,
                statusDTO,
                currentUserEmail);
    }
}
