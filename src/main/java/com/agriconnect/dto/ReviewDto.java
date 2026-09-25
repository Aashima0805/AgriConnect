package com.agriconnect.dto;

import com.agriconnect.entity.Review;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReviewDto {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long productId;
    private String productName;
    private Long farmerId;
    private String farmerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewDto() {}

    public static ReviewDto fromEntity(Review r) {
        if (r == null) return null;
        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        if (r.getCustomer() != null) {
            dto.setCustomerId(r.getCustomer().getId());
            dto.setCustomerName(r.getCustomer().getName());
        }
        if (r.getProduct() != null) {
            dto.setProductId(r.getProduct().getId());
            dto.setProductName(r.getProduct().getName());
            if (r.getProduct().getFarmer() != null) {
                dto.setFarmerId(r.getProduct().getFarmer().getId());
                dto.setFarmerName(r.getProduct().getFarmer().getName());
            }
        }
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Object> getCustomer() {
        if (customerId == null && customerName == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", customerId);
        map.put("name", customerName);
        return map;
    }

    public Map<String, Object> getProduct() {
        if (productId == null && productName == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", productId);
        map.put("name", productName);
        if (farmerId != null || farmerName != null) {
            Map<String, Object> farmerMap = new LinkedHashMap<>();
            farmerMap.put("id", farmerId);
            farmerMap.put("name", farmerName);
            map.put("farmer", farmerMap);
        }
        return map;
    }
}
