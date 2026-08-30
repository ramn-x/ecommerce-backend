package com.ecommerce.ecommerce_backend.Mapper;

import com.ecommerce.ecommerce_backend.DTO.ProductDTO;
import com.ecommerce.ecommerce_backend.DTO.ProductRequestDTO;
import com.ecommerce.ecommerce_backend.Entity.Product;

public class ProductMapper {


    public static Product toEntity(ProductRequestDTO dto){
        Product product = new Product();

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setQuantity(dto.getQuantity());

        return product;
    }

    public static ProductDTO toDTO(Product product) {

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());

        return dto;

    }
}
