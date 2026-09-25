package com.agriconnect.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartResponseDto {

    private Long customerId;
    private List<CartItemDto> items = new ArrayList<>();
    private int totalItems;
    private BigDecimal cartTotal = BigDecimal.ZERO;

    public CartResponseDto() {
    }

    public CartResponseDto(Long customerId, List<CartItemDto> items) {
        this.customerId = customerId;
        this.items = items != null ? items : new ArrayList<>();
        this.totalItems = this.items.size();
        this.cartTotal = this.items.stream()
                .map(CartItemDto::getItemTotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(BigDecimal cartTotal) {
        this.cartTotal = cartTotal;
    }
}
