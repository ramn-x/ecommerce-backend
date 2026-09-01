package com.ecommerce.ecommerce_backend.Repository;

import com.ecommerce.ecommerce_backend.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Integer> {
     List<Cart> findByUserId(Integer userId);
     Optional<Cart>findByUserIdAndProductId(
             Integer userid,
             Integer productid );

}
