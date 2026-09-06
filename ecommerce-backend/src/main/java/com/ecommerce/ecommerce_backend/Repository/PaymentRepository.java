package com.ecommerce.ecommerce_backend.Repository;

import com.ecommerce.ecommerce_backend.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    List<Payment> findByOrderId (Integer orderId);
}
