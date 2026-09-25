package com.agriconnect.dto;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminDashboardDto {

    private long totalUsers;
    private long totalFarmers;
    private long totalCustomers;
    private long totalDonors;
    private long totalProducts;
    private long totalOrders;
    private long totalGrievances;
    private long openGrievances;
    private long partiallyFundedGrievances;
    private long fundedGrievances;
    private long closedGrievances;
    private Map<String, Long> grievancesByStatus = new LinkedHashMap<>();
    private long totalDonations;
    private BigDecimal totalDonationAmount = BigDecimal.ZERO;
    private long totalEquipment;

    public AdminDashboardDto() {}

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalFarmers() {
        return totalFarmers;
    }

    public void setTotalFarmers(long totalFarmers) {
        this.totalFarmers = totalFarmers;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalDonors() {
        return totalDonors;
    }

    public void setTotalDonors(long totalDonors) {
        this.totalDonors = totalDonors;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalGrievances() {
        return totalGrievances;
    }

    public void setTotalGrievances(long totalGrievances) {
        this.totalGrievances = totalGrievances;
    }

    public long getOpenGrievances() {
        return openGrievances;
    }

    public void setOpenGrievances(long openGrievances) {
        this.openGrievances = openGrievances;
    }

    public long getPartiallyFundedGrievances() {
        return partiallyFundedGrievances;
    }

    public void setPartiallyFundedGrievances(long partiallyFundedGrievances) {
        this.partiallyFundedGrievances = partiallyFundedGrievances;
    }

    public long getFundedGrievances() {
        return fundedGrievances;
    }

    public void setFundedGrievances(long fundedGrievances) {
        this.fundedGrievances = fundedGrievances;
    }

    public long getClosedGrievances() {
        return closedGrievances;
    }

    public void setClosedGrievances(long closedGrievances) {
        this.closedGrievances = closedGrievances;
    }

    public Map<String, Long> getGrievancesByStatus() {
        return grievancesByStatus;
    }

    public void setGrievancesByStatus(Map<String, Long> grievancesByStatus) {
        this.grievancesByStatus = grievancesByStatus;
    }

    public long getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(long totalDonations) {
        this.totalDonations = totalDonations;
    }

    public BigDecimal getTotalDonationAmount() {
        return totalDonationAmount;
    }

    public void setTotalDonationAmount(BigDecimal totalDonationAmount) {
        this.totalDonationAmount = totalDonationAmount;
    }

    public long getTotalEquipment() {
        return totalEquipment;
    }

    public void setTotalEquipment(long totalEquipment) {
        this.totalEquipment = totalEquipment;
    }
}
