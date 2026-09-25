package com.agriconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "market_prices")
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cropName;

    @Column(nullable = false)
    private String market;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String unit; // e.g. "quintal", "kg"

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Boolean isDemo = true;

    private String category; // Grains, Vegetables, Oil Seeds, Pulses, Commercial

    private String state;

    private String priceType; // "MSP (Minimum Support Price)", "Mandi Market Rate"

    public MarketPrice() {
    }

    public MarketPrice(String cropName, String market, BigDecimal price, String unit, LocalDate date, Boolean isDemo, String category, String state, String priceType) {
        this.cropName = cropName;
        this.market = market;
        this.price = price;
        this.unit = unit;
        this.date = date;
        this.isDemo = isDemo;
        this.category = category;
        this.state = state;
        this.priceType = priceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getIsDemo() {
        return isDemo;
    }

    public void setIsDemo(Boolean isDemo) {
        this.isDemo = isDemo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
}
