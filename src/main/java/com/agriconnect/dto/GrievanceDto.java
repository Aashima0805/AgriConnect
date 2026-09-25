package com.agriconnect.dto;

import com.agriconnect.entity.Grievance;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GrievanceDto {

    private Long id;
    private Long farmerId;
    private String farmerName;
    private String farmerPhone;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String category;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "1.00", message = "Target amount must be at least 1")
    private BigDecimal targetAmount;

    private BigDecimal receivedAmount = BigDecimal.ZERO;
    private String status = "OPEN";
    private String location;
    private LocalDateTime createdAt;

    public GrievanceDto() {
    }

    public static GrievanceDto fromEntity(Grievance g) {
        if (g == null) return null;
        GrievanceDto dto = new GrievanceDto();
        dto.setId(g.getId());
        if (g.getFarmer() != null) {
            dto.setFarmerId(g.getFarmer().getId());
            dto.setFarmerName(g.getFarmer().getName());
            dto.setFarmerPhone(g.getFarmer().getPhone());
        }
        dto.setTitle(g.getTitle());
        dto.setDescription(g.getDescription());
        dto.setCategory(g.getCategory());
        dto.setTargetAmount(g.getTargetAmount());
        dto.setReceivedAmount(g.getReceivedAmount());
        dto.setStatus(g.getStatus());
        dto.setLocation(g.getLocation());
        dto.setCreatedAt(g.getCreatedAt());
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.util.Map<String, Object> getFarmer() {
        if (farmerId == null && farmerName == null) {
            return null;
        }
        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("id", farmerId);
        map.put("name", farmerName);
        map.put("phone", farmerPhone);
        return map;
    }
}
