package com.agriconnect.dto;

import com.agriconnect.entity.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    private Long id;
    private Long farmerId;
    private String farmerName;
    private String farmerPhone;
    private String farmerAddress;
    private Double farmerLatitude;
    private Double farmerLongitude;
    private Double distanceKm;

    @NotBlank(message = "Product name is required")
    private String name;

    private String category;
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private Double quantity;

    private String unit = "kg";
    private Boolean available = true;
    private String imageUrl;
    private LocalDateTime createdAt;

    public ProductDto() {
    }

    public static ProductDto fromEntity(Product p) {
        if (p == null) return null;
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        if (p.getFarmer() != null) {
            dto.setFarmerId(p.getFarmer().getId());
            dto.setFarmerName(p.getFarmer().getName());
            dto.setFarmerPhone(p.getFarmer().getPhone());
            dto.setFarmerAddress(p.getFarmer().getAddress());
            dto.setFarmerLatitude(p.getFarmer().getLatitude());
            dto.setFarmerLongitude(p.getFarmer().getLongitude());
        }
        dto.setName(p.getName());
        dto.setCategory(p.getCategory());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setQuantity(p.getQuantity());
        dto.setUnit(p.getUnit());
        dto.setAvailable(p.isAvailable());
        dto.setImageUrl(p.getImageUrl());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    public static ProductDto fromEntity(Product p, Double distanceKm) {
        ProductDto dto = fromEntity(p);
        if (dto != null) {
            dto.setDistanceKm(distanceKm);
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getFarmerPhone() {
        return farmerPhone;
    }

    public void setFarmerPhone(String farmerPhone) {
        this.farmerPhone = farmerPhone;
    }

    public String getFarmerAddress() {
        return farmerAddress;
    }

    public void setFarmerAddress(String farmerAddress) {
        this.farmerAddress = farmerAddress;
    }

    public String getFarmerLocation() {
        return farmerAddress;
    }

    public void setFarmerLocation(String farmerLocation) {
        this.farmerAddress = farmerLocation;
    }

    public Double getFarmerLatitude() {
        return farmerLatitude;
    }

    public void setFarmerLatitude(Double farmerLatitude) {
        this.farmerLatitude = farmerLatitude;
    }

    public Double getFarmerLongitude() {
        return farmerLongitude;
    }

    public void setFarmerLongitude(Double farmerLongitude) {
        this.farmerLongitude = farmerLongitude;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getDistance() {
        return distanceKm;
    }

    public void setDistance(Double distance) {
        this.distanceKm = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
