package com.ecommerce.ecommerce_backend.DTO;

import lombok.Data;

@Data
public class LoginResponseDTO {

    public String token;
    public LoginResponseDTO(String token){
        this.token=token;
    }
}
