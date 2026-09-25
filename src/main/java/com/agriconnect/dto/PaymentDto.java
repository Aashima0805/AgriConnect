package com.agriconnect.dto;

import com.agriconnect.entity.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    private Long id;
    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private String method;
    private String status; // SUCCESS, FAILED, PENDING
    private String transactionReference;
    private Boolean isMock = true;
    private String message;
    private LocalDateTime paymentDate;
    private String disclaimer = "Academic Prototype Mock Payment - No actual financial charge processed.";

    public PaymentDto() {
    }

    public static PaymentDto fromEntity(Payment payment) {
        if (payment == null) return null;
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        if (payment.getOrder() != null) {
            dto.setOrderId(payment.getOrder().getId());
            if (payment.getOrder().getCustomer() != null) {
                dto.setCustomerId(payment.getOrder().getCustomer().getId());
            }
        }
        dto.setAmount(payment.getAmount());
        dto.setMethod(payment.getMethod() != null ? payment.getMethod().name() : "UPI");
        dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : "SUCCESS");
        dto.setTransactionReference(payment.getTransactionReference());
        dto.setIsMock(payment.getIsMock());
        dto.setPaymentDate(payment.getPaymentDate());
        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            dto.setMessage("Mock payment processed successfully.");
        } else {
            dto.setMessage("Mock payment simulation failed or was declined.");
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Boolean getIsMock() {
        return isMock;
    }

    public void setIsMock(Boolean isMock) {
        this.isMock = isMock;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
