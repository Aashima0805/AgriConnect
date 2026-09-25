package com.agriconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grievances")
public class Grievance {

    public enum Status {
        OPEN,
        PARTIALLY_FUNDED,
        FUNDED,
        CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String category; // Flood / Rain Damage, Drought, Crop Disease, Pest Damage, Equipment Loss, Other

    @Column(nullable = false)
    private BigDecimal targetAmount;

    @Column(nullable = false)
    private BigDecimal receivedAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN, PARTIALLY_FUNDED, FUNDED, CLOSED

    private String location;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Grievance() {
    }

    public Grievance(User farmer, String title, String description, String category, BigDecimal targetAmount, String location) {
        this.farmer = farmer;
        this.title = title;
        this.description = description;
        this.category = category;
        this.targetAmount = targetAmount;
        this.receivedAmount = BigDecimal.ZERO;
        this.status = "OPEN";
        this.location = location;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.receivedAmount == null) {
            this.receivedAmount = BigDecimal.ZERO;
        }
        if (this.status == null) {
            this.status = "OPEN";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFarmer() {
        return farmer;
    }

    public void setFarmer(User farmer) {
        this.farmer = farmer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
