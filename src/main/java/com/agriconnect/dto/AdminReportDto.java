package com.agriconnect.dto;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminReportDto {

    private Map<String, Long> usersByRole = new LinkedHashMap<>();
    private Map<String, Long> productsByCategory = new LinkedHashMap<>();
    private long totalProducts;
    private Map<String, Long> ordersByStatus = new LinkedHashMap<>();
    private long totalOrders;
    private Map<String, Long> grievancesByStatus = new LinkedHashMap<>();
    private long totalGrievances;
    private BigDecimal totalDonations = BigDecimal.ZERO;
    private long totalDonationCount;
    private long totalEquipment;

    public AdminReportDto() {}

    public Map<String, Long> getUsersByRole() {
        return usersByRole;
    }

    public void setUsersByRole(Map<String, Long> usersByRole) {
        this.usersByRole = usersByRole;
    }

    public Map<String, Long> getProductsByCategory() {
        return productsByCategory;
    }

    public void setProductsByCategory(Map<String, Long> productsByCategory) {
        this.productsByCategory = productsByCategory;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Map<String, Long> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void setOrdersByStatus(Map<String, Long> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Map<String, Long> getGrievancesByStatus() {
        return grievancesByStatus;
    }

    public void setGrievancesByStatus(Map<String, Long> grievancesByStatus) {
        this.grievancesByStatus = grievancesByStatus;
    }

    public long getTotalGrievances() {
        return totalGrievances;
    }

    public void setTotalGrievances(long totalGrievances) {
        this.totalGrievances = totalGrievances;
    }

    public BigDecimal getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(BigDecimal totalDonations) {
        this.totalDonations = totalDonations;
    }

    public long getTotalDonationCount() {
        return totalDonationCount;
    }

    public void setTotalDonationCount(long totalDonationCount) {
        this.totalDonationCount = totalDonationCount;
    }

    public long getTotalEquipment() {
        return totalEquipment;
    }

    public void setTotalEquipment(long totalEquipment) {
        this.totalEquipment = totalEquipment;
    }
}
