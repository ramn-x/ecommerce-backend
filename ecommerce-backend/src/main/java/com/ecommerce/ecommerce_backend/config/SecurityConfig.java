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

    // BCrypt password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Load users from MySQL database
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

    // Security configuration
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
                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/users/login"
                                ).permitAll()

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/users"
                                ).permitAll()

                                // USER MANAGEMENT
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
                                // PRODUCTS
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/products/**"
                                ).hasAnyRole("USER", "ADMIN")

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

                                // ORDERS
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/orders"
                                ).hasAnyRole("USER", "ADMIN")

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/orders/**"
                                ).hasAnyRole("USER", "ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/orders/**"
                                ).hasRole("ADMIN")

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/orders/**"
                                ).hasRole("ADMIN")
                                // CART
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/cart"
                                ).hasAnyRole("USER", "ADMIN")

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/cart"
                                ).hasAnyRole("USER", "ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/cart/**"
                                ).hasAnyRole("USER", "ADMIN")

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/cart/**"
                                ).hasAnyRole("USER", "ADMIN")


                                // EVERYTHING ELSE
                                .anyRequest().authenticated()

                        )

                // JWT filter runs before Spring's authentication filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}