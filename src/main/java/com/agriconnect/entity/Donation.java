package com.agriconnect.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grievance_id", nullable = false)
    private Grievance grievance;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentStatus = "SUCCESS";

    @Column(nullable = false, unique = true)
    private String receiptNumber;

    @Column(nullable = false)
    private String transactionReference;

    @Column(nullable = false, updatable = false)
    private LocalDateTime donatedAt = LocalDateTime.now();

    public Donation() {}

    public Donation(User donor, Grievance grievance, BigDecimal amount, String paymentStatus, String receiptNumber, String transactionReference) {
        this.donor = donor;
        this.grievance = grievance;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.receiptNumber = receiptNumber;
        this.transactionReference = transactionReference;
        this.donatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.donatedAt == null) {
            this.donatedAt = LocalDateTime.now();
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = "SUCCESS";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getDonor() { return donor; }
    public void setDonor(User donor) { this.donor = donor; }

    public Grievance getGrievance() { return grievance; }
    public void setGrievance(Grievance grievance) { this.grievance = grievance; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public LocalDateTime getDonatedAt() { return donatedAt; }
    public void setDonatedAt(LocalDateTime donatedAt) { this.donatedAt = donatedAt; }
}
