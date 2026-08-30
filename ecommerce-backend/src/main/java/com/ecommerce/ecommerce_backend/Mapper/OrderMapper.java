package com.ecommerce.ecommerce_backend.Mapper;

import com.ecommerce.ecommerce_backend.DTO.OrderDTO;
import com.ecommerce.ecommerce_backend.DTO.OrderRequestDTO;
import com.ecommerce.ecommerce_backend.Entity.Order;

public class OrderMapper {

    public static OrderDTO toDTO(Order order) {

        OrderDTO dto = new OrderDTO();

        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setProductId(order.getProductId());
        dto.setQuantity(order.getQuantity());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderDate(order.getOrderDate());

        return dto;
    }

    public static Order toEntity(OrderRequestDTO  dto) {

        Order order = new Order();

        order.setUserId(dto.getUserId());
        order.setProductId(dto.getProductId());
        order.setQuantity(dto.getQuantity());

        return order;
    }
}