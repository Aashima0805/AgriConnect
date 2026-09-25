package com.agriconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "farm_details")
public class FarmDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "farmer_id", unique = true, nullable = false)
    private User farmer;

    private Double landArea;

    private String landUnit; // acres, hectares, etc.

    private String location;

    private String crops;

    private String soilType;

    private String irrigationType;

    public FarmDetails() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getFarmer() { return farmer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }

    public Double getLandArea() { return landArea; }
    public void setLandArea(Double landArea) { this.landArea = landArea; }

    public String getLandUnit() { return landUnit; }
    public void setLandUnit(String landUnit) { this.landUnit = landUnit; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCrops() { return crops; }
    public void setCrops(String crops) { this.crops = crops; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getIrrigationType() { return irrigationType; }
    public void setIrrigationType(String irrigationType) { this.irrigationType = irrigationType; }
}
