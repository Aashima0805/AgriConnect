package com.agriconnect.dto;

import com.agriconnect.entity.CartItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CartItemDto {

    private Long id;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productName;
    private String productCategory;
    private String productImageUrl;
    private BigDecimal unitPrice;
    private String unit;
    private Double stockAvailable;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private Double quantity;

    private BigDecimal itemTotal;

    public CartItemDto() {
    }

    public static CartItemDto fromEntity(CartItem item) {
        if (item == null) return null;
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        if (item.getCustomer() != null) {
            dto.setCustomerId(item.getCustomer().getId());
        }
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setProductCategory(item.getProduct().getCategory());
            dto.setProductImageUrl(item.getProduct().getImageUrl());
            dto.setUnitPrice(item.getProduct().getPrice());
            dto.setUnit(item.getProduct().getUnit());
            dto.setStockAvailable(item.getProduct().getQuantity());
            if (item.getProduct().getPrice() != null && item.getQuantity() > 0) {
                dto.setItemTotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        dto.setQuantity(item.getQuantity());
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

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(Double stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }
}
