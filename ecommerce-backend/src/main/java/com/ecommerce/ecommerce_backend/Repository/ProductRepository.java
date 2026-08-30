package com.ecommerce.ecommerce_backend.Repository;

import com.ecommerce.ecommerce_backend.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product>findByPriceBetween(double minPrice, double maxPrice,Pageable pageable);
}
