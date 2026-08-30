package com.ecommerce.ecommerce_backend.Repository;

import com.ecommerce.ecommerce_backend.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Integer> {

    Page<Order> findByUserId(Integer userId , Pageable pageable);
    Page<Order> findByProductId(Integer productId, Pageable pageable);
}
