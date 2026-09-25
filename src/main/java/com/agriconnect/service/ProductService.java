package com.agriconnect.service;

import com.agriconnect.dto.ProductDto;
import com.agriconnect.entity.Product;
import com.agriconnect.entity.User;
import com.agriconnect.repository.ProductRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public ProductService(ProductRepository productRepo, UserRepository userRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public List<ProductDto> listAvailableProducts(String category, String query) {
        List<Product> products;
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("All")) {
            products = productRepo.findByCategoryIgnoreCaseAndAvailableTrue(category.trim());
        } else if (query != null && !query.isBlank()) {
            products = productRepo.findByNameContainingIgnoreCaseAndAvailableTrue(query.trim());
        } else {
            products = productRepo.findByAvailableTrue();
        }
        return products.stream().map(ProductDto::fromEntity).collect(Collectors.toList());
    }

    public Optional<ProductDto> getProductById(Long id) {
        return productRepo.findById(id).map(ProductDto::fromEntity);
    }

    public List<ProductDto> getProductsByFarmer(Long farmerId) {
        if (farmerId == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }
        return productRepo.findByFarmerId(farmerId).stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto addProduct(Long farmerId, ProductDto dto) {
        if (farmerId == null) {
            throw new IllegalArgumentException("Farmer ID is required.");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0.");
        }
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Product quantity must be greater than 0.");
        }

        User farmer = userRepo.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found with id: " + farmerId));

        Product product = new Product();
        product.setFarmer(farmer);
        product.setName(dto.getName().trim());
        product.setCategory(dto.getCategory() != null ? dto.getCategory().trim() : "Other");
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setUnit(dto.getUnit() != null && !dto.getUnit().isBlank() ? dto.getUnit().trim() : "kg");
        product.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : true);
        product.setImageUrl(dto.getImageUrl());

        Product saved = productRepo.save(product);
        return ProductDto.fromEntity(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long productId, Long farmerId, ProductDto dto) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        if (farmerId != null && !product.getFarmer().getId().equals(farmerId)) {
            throw new SecurityException("Unauthorized: you can only update your own products.");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            product.setName(dto.getName().trim());
        }
        if (dto.getCategory() != null) {
            product.setCategory(dto.getCategory().trim());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            if (dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Product price must be greater than 0.");
            }
            product.setPrice(dto.getPrice());
        }
        if (dto.getQuantity() != null) {
            if (dto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Product quantity must be greater than 0.");
            }
            product.setQuantity(dto.getQuantity());
        }
        if (dto.getUnit() != null && !dto.getUnit().isBlank()) {
            product.setUnit(dto.getUnit().trim());
        }
        if (dto.getAvailable() != null) {
            product.setAvailable(dto.getAvailable());
        }
        if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
        }

        Product updated = productRepo.save(product);
        return ProductDto.fromEntity(updated);
    }

    @Transactional
    public void deleteProduct(Long productId, Long farmerId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        if (farmerId != null && !product.getFarmer().getId().equals(farmerId)) {
            throw new SecurityException("Unauthorized: you can only delete your own products.");
        }

        productRepo.delete(product);
    }

    public List<ProductDto> getNearbyProducts(Long customerId, Double radiusKm) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        if (radiusKm == null || radiusKm <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0.");
        }

        User customer = userRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));

        if (customer.getLatitude() == null || customer.getLongitude() == null) {
            throw new IllegalArgumentException("Customer coordinates are not configured.");
        }

        double customerLat = customer.getLatitude();
        double customerLon = customer.getLongitude();

        List<Product> availableProducts = productRepo.findByAvailableTrue();
        List<ProductDto> nearbyProducts = new ArrayList<>();

        for (Product product : availableProducts) {
            User farmer = product.getFarmer();
            if (farmer == null || farmer.getRole() != User.Role.FARMER) {
                continue;
            }
            if (farmer.getLatitude() == null || farmer.getLongitude() == null) {
                continue;
            }

            double distance = calculateHaversineDistance(
                    customerLat, customerLon,
                    farmer.getLatitude(), farmer.getLongitude()
            );

            if (distance <= radiusKm) {
                double roundedDistance = Math.round(distance * 100.0) / 100.0;
                ProductDto dto = ProductDto.fromEntity(product, roundedDistance);
                nearbyProducts.add(dto);
            }
        }

        nearbyProducts.sort(Comparator.comparingDouble(ProductDto::getDistanceKm));
        return nearbyProducts;
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
