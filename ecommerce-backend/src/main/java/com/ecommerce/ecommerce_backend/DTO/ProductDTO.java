package com.ecommerce.ecommerce_backend.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductDTO {

    private Integer id;
    @NotBlank
    private String name;
    @Positive
    private double price;
    private String description;
    @Min(0)
    private int quantity;

}
