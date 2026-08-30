package com.ecommerce.ecommerce_backend.Repository;

import com.ecommerce.ecommerce_backend.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Page<User> findByNameContainingIgnoreCase(
            String name, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    Optional<User> findByEmail(String email);
}