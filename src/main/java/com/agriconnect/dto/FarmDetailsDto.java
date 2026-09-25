package com.agriconnect.dto;

import com.agriconnect.entity.FarmDetails;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class FarmDetailsDto {

    private Long id;
    private Long farmerId;
    private String farmerName;

    @NotNull(message = "Land area is required")
    @DecimalMin(value = "0.01", message = "Land area must be greater than 0")
    private Double landArea;

    private String landUnit = "acres";
    private String location;
    private String crops;
    private String soilType;
    private String irrigationType;

    public FarmDetailsDto() {
    }

    public static FarmDetailsDto fromEntity(FarmDetails entity) {
        if (entity == null) return null;
        FarmDetailsDto dto = new FarmDetailsDto();
        dto.setId(entity.getId());
        if (entity.getFarmer() != null) {
            dto.setFarmerId(entity.getFarmer().getId());
            dto.setFarmerName(entity.getFarmer().getName());
        }
        dto.setLandArea(entity.getLandArea());
        dto.setLandUnit(entity.getLandUnit());
        dto.setLocation(entity.getLocation());
        dto.setCrops(entity.getCrops());
        dto.setSoilType(entity.getSoilType());
        dto.setIrrigationType(entity.getIrrigationType());
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

    public Double getLandArea() {
        return landArea;
    }

    public void setLandArea(Double landArea) {
        this.landArea = landArea;
    }

    public String getLandUnit() {
        return landUnit;
    }

    public void setLandUnit(String landUnit) {
        this.landUnit = landUnit;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCrops() {
        return crops;
    }

    public void setCrops(String crops) {
        this.crops = crops;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getIrrigationType() {
        return irrigationType;
    }

    public void setIrrigationType(String irrigationType) {
        this.irrigationType = irrigationType;
    }
}
