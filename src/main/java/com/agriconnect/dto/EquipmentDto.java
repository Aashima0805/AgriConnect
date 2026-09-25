package com.agriconnect.dto;

import com.agriconnect.entity.Equipment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EquipmentDto {

    private Long id;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;

    @NotBlank(message = "Equipment name is required")
    private String name;

    private String type;
    private String description;

    @NotNull(message = "Rental price is required")
    @DecimalMin(value = "0.01", message = "Rental price must be greater than 0")
    private BigDecimal rentalPricePerDay;

    private String location;
    private Boolean available = true;
    private LocalDateTime createdAt;

    public EquipmentDto() {
    }

    public static EquipmentDto fromEntity(Equipment e) {
        if (e == null) return null;
        EquipmentDto dto = new EquipmentDto();
        dto.setId(e.getId());
        if (e.getOwner() != null) {
            dto.setOwnerId(e.getOwner().getId());
            dto.setOwnerName(e.getOwner().getName());
            dto.setOwnerPhone(e.getOwner().getPhone());
        }
        dto.setName(e.getName());
        dto.setType(e.getType());
        dto.setDescription(e.getDescription());
        dto.setRentalPricePerDay(e.getRentalPricePerDay());
        dto.setLocation(e.getLocation());
        dto.setAvailable(e.isAvailable());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public void setRentalPricePerDay(BigDecimal rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
