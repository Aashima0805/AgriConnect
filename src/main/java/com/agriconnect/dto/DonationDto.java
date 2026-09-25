package com.agriconnect.dto;

import com.agriconnect.entity.Donation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class DonationDto {

    private Long id;
    private Long donorId;
    private String donorName;
    private String donorEmail;
    private Long grievanceId;
    private String grievanceTitle;
    private String grievanceCategory;
    private BigDecimal grievanceTargetAmount;
    private BigDecimal grievanceReceivedAmount;
    private String grievanceStatus;
    private Long farmerId;
    private String farmerName;
    private String farmerPhone;
    private BigDecimal amount;
    private String paymentStatus;
    private String receiptNumber;
    private String transactionReference;
    private LocalDateTime donatedAt;
    private Boolean isMock = true;
    private String disclaimer = "Academic Prototype Mock Donation - No real financial transaction was processed.";
    private String message;

    public DonationDto() {}

    public static DonationDto fromEntity(Donation donation) {
        if (donation == null) return null;
        DonationDto dto = new DonationDto();
        dto.setId(donation.getId());
        if (donation.getDonor() != null) {
            dto.setDonorId(donation.getDonor().getId());
            dto.setDonorName(donation.getDonor().getName());
            dto.setDonorEmail(donation.getDonor().getEmail());
        }
        if (donation.getGrievance() != null) {
            dto.setGrievanceId(donation.getGrievance().getId());
            dto.setGrievanceTitle(donation.getGrievance().getTitle());
            dto.setGrievanceCategory(donation.getGrievance().getCategory());
            dto.setGrievanceTargetAmount(donation.getGrievance().getTargetAmount());
            dto.setGrievanceReceivedAmount(donation.getGrievance().getReceivedAmount());
            dto.setGrievanceStatus(donation.getGrievance().getStatus());
            if (donation.getGrievance().getFarmer() != null) {
                dto.setFarmerId(donation.getGrievance().getFarmer().getId());
                dto.setFarmerName(donation.getGrievance().getFarmer().getName());
                dto.setFarmerPhone(donation.getGrievance().getFarmer().getPhone());
            }
        }
        dto.setAmount(donation.getAmount());
        dto.setPaymentStatus(donation.getPaymentStatus());
        dto.setReceiptNumber(donation.getReceiptNumber());
        dto.setTransactionReference(donation.getTransactionReference());
        dto.setDonatedAt(donation.getDonatedAt());
        dto.setIsMock(true);
        dto.setDisclaimer("Academic Prototype Mock Donation - No real financial transaction was processed.");
        dto.setMessage("Mock donation of ₹" + donation.getAmount() + " processed successfully. Receipt: " + donation.getReceiptNumber());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public Long getGrievanceId() {
        return grievanceId;
    }

    public void setGrievanceId(Long grievanceId) {
        this.grievanceId = grievanceId;
    }

    public String getGrievanceTitle() {
        return grievanceTitle;
    }

    public void setGrievanceTitle(String grievanceTitle) {
        this.grievanceTitle = grievanceTitle;
    }

    public String getGrievanceCategory() {
        return grievanceCategory;
    }

    public void setGrievanceCategory(String grievanceCategory) {
        this.grievanceCategory = grievanceCategory;
    }

    public BigDecimal getGrievanceTargetAmount() {
        return grievanceTargetAmount;
    }

    public void setGrievanceTargetAmount(BigDecimal grievanceTargetAmount) {
        this.grievanceTargetAmount = grievanceTargetAmount;
    }

    public BigDecimal getGrievanceReceivedAmount() {
        return grievanceReceivedAmount;
    }

    public void setGrievanceReceivedAmount(BigDecimal grievanceReceivedAmount) {
        this.grievanceReceivedAmount = grievanceReceivedAmount;
    }

    public String getGrievanceStatus() {
        return grievanceStatus;
    }

    public void setGrievanceStatus(String grievanceStatus) {
        this.grievanceStatus = grievanceStatus;
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

    public String getFarmerPhone() {
        return farmerPhone;
    }

    public void setFarmerPhone(String farmerPhone) {
        this.farmerPhone = farmerPhone;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getDonatedAt() {
        return donatedAt;
    }

    public void setDonatedAt(LocalDateTime donatedAt) {
        this.donatedAt = donatedAt;
    }

    public Boolean getIsMock() {
        return isMock;
    }

    public void setIsMock(Boolean isMock) {
        this.isMock = isMock;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return paymentStatus != null ? paymentStatus : "SUCCESS";
    }

    public Long getDonationId() {
        return id;
    }

    public Map<String, Object> getGrievance() {
        if (grievanceId == null && grievanceTitle == null) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", grievanceId);
        map.put("title", grievanceTitle);
        map.put("category", grievanceCategory);
        map.put("targetAmount", grievanceTargetAmount);
        map.put("receivedAmount", grievanceReceivedAmount);
        map.put("status", grievanceStatus);
        if (farmerId != null || farmerName != null) {
            Map<String, Object> farmerMap = new LinkedHashMap<>();
            farmerMap.put("id", farmerId);
            farmerMap.put("name", farmerName);
            farmerMap.put("phone", farmerPhone);
            map.put("farmer", farmerMap);
        }
        return map;
    }

    public Map<String, Object> getDonor() {
        if (donorId == null && donorName == null) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", donorId);
        map.put("name", donorName);
        map.put("email", donorEmail);
        return map;
    }
}
