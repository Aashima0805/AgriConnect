package com.agriconnect.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DonationRequest {

    @NotNull(message = "Donor ID is required")
    private Long donorId;

    @NotNull(message = "Grievance ID is required")
    private Long grievanceId;

    @NotNull(message = "Donation amount is required")
    @DecimalMin(value = "1.00", message = "Donation amount must be at least ₹1.00")
    private BigDecimal amount;

    private String paymentMethod = "MOCK_UPI"; // MOCK_UPI, MOCK_CARD, MOCK_NET_BANKING

    public DonationRequest() {}

    public DonationRequest(Long donorId, Long grievanceId, BigDecimal amount) {
        this.donorId = donorId;
        this.grievanceId = grievanceId;
        this.amount = amount;
    }

    public DonationRequest(Long donorId, Long grievanceId, BigDecimal amount, String paymentMethod) {
        this.donorId = donorId;
        this.grievanceId = grievanceId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public Long getGrievanceId() {
        return grievanceId;
    }

    public void setGrievanceId(Long grievanceId) {
        this.grievanceId = grievanceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
