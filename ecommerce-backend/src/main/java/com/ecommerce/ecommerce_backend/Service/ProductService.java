package com.ecommerce.ecommerce_backend.Service;


import com.ecommerce.ecommerce_backend.DTO.ProductDTO;
import com.ecommerce.ecommerce_backend.DTO.ProductRequestDTO;
import com.ecommerce.ecommerce_backend.Entity.Product;
import com.ecommerce.ecommerce_backend.Exception.ProductNotFoundException;
import com.ecommerce.ecommerce_backend.Mapper.ProductMapper;
import com.ecommerce.ecommerce_backend.Repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }



    public ProductDTO addProduct(ProductRequestDTO productRequestDTO) {

        Product product = ProductMapper.toEntity(productRequestDTO);

        Product savedProduct = productRepository.save(product);

        return ProductMapper.toDTO(savedProduct);
    }

    public Page<ProductDTO> getAllProduct(Pageable pageable) {

        return productRepository.findAll(pageable)
                .map(ProductMapper::toDTO);
    }

//    read by id

    public ProductDTO getProductById(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id));

        return ProductMapper.toDTO(product);
    }

//    delete
    public void deleteProduct(Integer id){

        Product product= productRepository.findById(id)
              .orElseThrow(()->new ProductNotFoundException(
                      "Product not found with id: " + id
              ));
        productRepository.delete(product);
    }

//    Update
public ProductDTO updateProduct(
        Integer id,
        ProductRequestDTO productRequestDTO) {

    Product existingProduct = productRepository.findById(id)
            .orElseThrow(() ->
                    new ProductNotFoundException(
                            "Product not found with id: " + id));

    existingProduct.setName(productRequestDTO.getName());
    existingProduct.setPrice(productRequestDTO.getPrice());
    existingProduct.setDescription(productRequestDTO.getDescription());
    existingProduct.setQuantity(productRequestDTO.getQuantity());

    Product updatedProduct = productRepository.save(existingProduct);

    return ProductMapper.toDTO(updatedProduct);
}
//    search
public Page<ProductDTO> searchProductsByName(String name, Pageable pageable) {
    Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
    return products.map(ProductMapper::toDTO);
}

//    filter price
   public Page<ProductDTO> filterByPrice(double minPrice, double maxPrice,Pageable pageable) {
       Page<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice,pageable);
       return products.map(ProductMapper::toDTO);
}
}
