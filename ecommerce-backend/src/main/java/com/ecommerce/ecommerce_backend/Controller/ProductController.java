package com.ecommerce.ecommerce_backend.Controller;

import com.ecommerce.ecommerce_backend.DTO.ProductDTO;
import com.ecommerce.ecommerce_backend.DTO.ProductRequestDTO;
import com.ecommerce.ecommerce_backend.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductDTO savedProduct = productService.addProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);

    }
    @GetMapping
    public Page<ProductDTO> getAllProduct(Pageable pageable){
        return productService.getAllProduct(pageable);
    }
    @GetMapping("/search")
    public Page<ProductDTO> searchProductsByName(
            @RequestParam String name, Pageable pageable) {
        return productService.searchProductsByName(name, pageable);
    }

    @GetMapping("/filter")
    public Page<ProductDTO> filterByPrice(
            @RequestParam double minPrice,@RequestParam double maxPrice,Pageable pageable){
        return productService.filterByPrice(minPrice,maxPrice,pageable);
    }
    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer id,@Valid @RequestBody ProductRequestDTO  productRequestDTO){
        ProductDTO updatedProduct = productService.updateProduct(id ,productRequestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

}
