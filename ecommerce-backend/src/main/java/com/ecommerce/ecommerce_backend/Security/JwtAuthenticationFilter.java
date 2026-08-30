package com.ecommerce.ecommerce_backend.Security;

import com.ecommerce.ecommerce_backend.Entity.User;
import com.ecommerce.ecommerce_backend.Repository.UserRepository;
import com.ecommerce.ecommerce_backend.Service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return "/users/login".equals(request.getServletPath())
                || "/error".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null
                && authHeader.startsWith("Bearer ")
                && authHeader.length() > 7) {

            String token = authHeader.substring(7);

            try {
                String email = jwtService.extractEmail(token);

                User user = userRepository.findByEmail(email)
                        .orElse(null);

                if (user != null) {
                    CustomUserDetails userDetails= new CustomUserDetails(user);
                    System.out.println("EMAIL: "+userDetails.getUsername());
                    System.out.println("ROLE: "+userDetails.getAuthorities());
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }

            } catch (JwtException | IllegalArgumentException ex) {
                // Invalid JWT: don't crash the request
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}