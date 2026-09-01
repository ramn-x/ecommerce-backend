package com.ecommerce.ecommerce_backend.Mapper;

import com.ecommerce.ecommerce_backend.DTO.CartDTO;
import com.ecommerce.ecommerce_backend.Entity.Cart;

public class CartMapper {


    public static CartDTO toDTO(Cart cart){
        CartDTO dto=new CartDTO();
        dto.setId(cart.getId());
        dto.setUserid(cart.getUser().getId());
        dto.setProductid(cart.getProduct().getId());
        dto.setQuantity(cart.getQuantity());
        return dto;
    }
}
