package com.agriconnect.dto;

import java.math.BigDecimal;

public class EquipmentRentalPaymentRequest {

    private Long rentalId;
    private Long renterId;
    private BigDecimal amount;
    private String method;
    private Boolean simulateSuccess;

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getSimulateSuccess() {
        return simulateSuccess;
    }

    public void setSimulateSuccess(Boolean simulateSuccess) {
        this.simulateSuccess = simulateSuccess;
    }
}