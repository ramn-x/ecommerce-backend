package com.ecommerce.ecommerce_backend.Mapper;

import com.ecommerce.ecommerce_backend.DTO.UserDTO;
import com.ecommerce.ecommerce_backend.DTO.UserRequestDTO;
import com.ecommerce.ecommerce_backend.Entity.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());


        return dto;
    }

    public static User toEntity(UserRequestDTO dto) {

        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());

        return user;
    }
} 