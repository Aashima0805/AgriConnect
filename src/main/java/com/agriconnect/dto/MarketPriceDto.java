package com.agriconnect.dto;

import com.agriconnect.entity.MarketPrice;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MarketPriceDto {

    private Long id;

    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotBlank(message = "Market name is required")
    private String market;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "Unit is required")
    private String unit = "quintal";

    private LocalDate date = LocalDate.now();

    private Boolean isDemo = true;

    private String category;
    private String state;
    private String priceType;
    private String disclaimer;

    public MarketPriceDto() {
    }

    public static MarketPriceDto fromEntity(MarketPrice entity) {
        if (entity == null) return null;
        MarketPriceDto dto = new MarketPriceDto();
        dto.setId(entity.getId());
        dto.setCropName(entity.getCropName());
        dto.setMarket(entity.getMarket());
        dto.setPrice(entity.getPrice());
        dto.setUnit(entity.getUnit());
        dto.setDate(entity.getDate());
        dto.setIsDemo(entity.getIsDemo());
        dto.setCategory(entity.getCategory());
        dto.setState(entity.getState());
        dto.setPriceType(entity.getPriceType());
        if (Boolean.TRUE.equals(entity.getIsDemo())) {
            dto.setDisclaimer("Demonstration / Sample price dataset. Not live government MSP.");
        }
        return dto;
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

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
