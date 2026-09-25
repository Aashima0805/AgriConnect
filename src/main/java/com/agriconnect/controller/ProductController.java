package com.agriconnect.controller;

import com.agriconnect.dto.ProductDto;
import com.agriconnect.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> listProducts(@RequestParam(required = false) String category,
                                         @RequestParam(required = false) String query) {
        return productService.listAvailableProducts(category, query);
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyProducts(@RequestParam(required = false) Long customerId,
                                               @RequestParam(required = false) Double radiusKm) {
        try {
            List<ProductDto> nearby = productService.getNearbyProducts(customerId, radiusKm);
            return ResponseEntity.ok(nearby);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/farmer/{farmerId}")
    public List<ProductDto> getFarmerProducts(@PathVariable Long farmerId) {
        return productService.getProductsByFarmer(farmerId);
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductDto dto) {
        try {
            ProductDto created = productService.addProduct(dto.getFarmerId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDto dto) {
        try {
            ProductDto updated = productService.updateProduct(id, dto.getFarmerId(), dto);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestParam(required = false) Long farmerId) {
        try {
            productService.deleteProduct(id, farmerId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Product deleted successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
