package com.agriconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"customer_id", "farmer_id", "order_id"})
})
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    private Long orderId;

    private int productQuality = 5; // 1-5

    private int deliveryExperience = 5; // 1-5

    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Rating() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public User getFarmer() { return farmer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public int getProductQuality() { return productQuality; }
    public void setProductQuality(int productQuality) { this.productQuality = productQuality; }

    public int getDeliveryExperience() { return deliveryExperience; }
    public void setDeliveryExperience(int deliveryExperience) { this.deliveryExperience = deliveryExperience; }

    public double getAverageScore() {
        return (productQuality + deliveryExperience) / 2.0;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
