package com.agriconnect.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public class FertilizerRequest {

    @NotBlank(message = "Crop name is required")
    private String cropName;

    private String soilType; // Alluvial, Black, Red, Sandy, Clay, Loam

    private Double nitrogen; // in kg/ha or ppm if available

    private Double phosphorus;

    private Double potassium;

    private Double ph;

    @DecimalMin(value = "0.01", message = "Land area must be greater than 0")
    private Double landArea = 1.0;

    private String landUnit = "acres";

    private String irrigationType;

    private String stage; // Sowing/Basal, Vegetative/Tillering, Flowering, Grain/Fruit Filling

    public FertilizerRequest() {
    }

    public FertilizerRequest(String cropName, String soilType, Double landArea, String stage) {
        this.cropName = cropName;
        this.soilType = soilType;
        this.landArea = landArea;
        this.stage = stage;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public Double getNitrogen() {
        return nitrogen;
    }

    public void setNitrogen(Double nitrogen) {
        this.nitrogen = nitrogen;
    }

    public Double getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(Double phosphorus) {
        this.phosphorus = phosphorus;
    }

    public Double getPotassium() {
        return potassium;
    }

    public void setPotassium(Double potassium) {
        this.potassium = potassium;
    }

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
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

    public String getIrrigationType() {
        return irrigationType;
    }

    public void setIrrigationType(String irrigationType) {
        this.irrigationType = irrigationType;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
