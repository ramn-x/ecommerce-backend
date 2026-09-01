package com.ecommerce.ecommerce_backend.config;

import com.ecommerce.ecommerce_backend.Repository.UserRepository;
import com.ecommerce.ecommerce_backend.Security.CustomUserDetails;
import com.ecommerce.ecommerce_backend.Security.JwtAuthenticationFilter;
import com.ecommerce.ecommerce_backend.Service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // =========================
    // BCrypt Password Encoder
    // =========================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================
    // Load User From MySQL
    // =========================
    @Bean
    public UserDetailsService userDetailsService(
            UserRepository userRepository) {

        return username -> userRepository.findByEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: "
                                        + username));
    }

    // =========================
    // Security Configuration
    // =========================
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            UserRepository userRepository) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userRepository
                );

        http
                // REST API
                .csrf(csrf -> csrf.disable())

                // JWT based authentication
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC
                        // =========================

                        // Login
                        .requestMatchers(
                                HttpMethod.POST,
                                "/users/login"
                        ).permitAll()

                        // Registration
                        .requestMatchers(
                                HttpMethod.POST,
                                "/users"
                        ).permitAll()


                        // =========================
                        // USER MANAGEMENT
                        // =========================

                        // Admin only
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/search"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/email"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/users/admin"
                        ).hasRole("ADMIN")


                        // =========================
                        // PRODUCTS
                        // =========================

                        // USER + ADMIN can view products
                        .requestMatchers(
                                HttpMethod.GET,
                                "/products/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // ADMIN can manage products
                        .requestMatchers(
                                HttpMethod.POST,
                                "/products"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/products/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/products/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // ORDERS
                        // =========================

                        // USER + ADMIN can create orders
                        .requestMatchers(
                                HttpMethod.POST,
                                "/orders"
                        ).hasAnyRole("USER", "ADMIN")

                        // ADMIN only: orders for a specific product
                        // IMPORTANT: must come before /orders/**
                        .requestMatchers(
                                HttpMethod.GET,
                                "/orders/product/**"
                        ).hasRole("ADMIN")

                        // USER + ADMIN can read orders
                        .requestMatchers(
                                HttpMethod.GET,
                                "/orders/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // ADMIN only
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/orders/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/orders/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // CART
                        // =========================

                        // USER + ADMIN can add to cart
                        .requestMatchers(
                                HttpMethod.POST,
                                "/cart"
                        ).hasAnyRole("USER", "ADMIN")

                        // USER + ADMIN can view cart
                        .requestMatchers(
                                HttpMethod.GET,
                                "/cart"
                        ).hasAnyRole("USER", "ADMIN")

                        // USER + ADMIN can update cart
                        // Ownership is checked in CartService
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/cart/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // USER + ADMIN can delete cart
                        // Ownership is checked in CartService
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/cart/**"
                        ).hasAnyRole("USER", "ADMIN")


                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest().authenticated()
                )

                // JWT filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}