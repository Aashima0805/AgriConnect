package com.agriconnect.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductReviewSummaryDto {

    private Long productId;
    private String productName;
    private Double averageRating = 0.0;
    private long totalReviews = 0;
    private long oneStarReviews = 0;
    private long twoStarReviews = 0;
    private long threeStarReviews = 0;
    private long fourStarReviews = 0;
    private long fiveStarReviews = 0;

    public ProductReviewSummaryDto() {}

    public ProductReviewSummaryDto(Long productId, String productName, Double averageRating, long totalReviews,
                                   long oneStarReviews, long twoStarReviews, long threeStarReviews,
                                   long fourStarReviews, long fiveStarReviews) {
        this.productId = productId;
        this.productName = productName;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.oneStarReviews = oneStarReviews;
        this.twoStarReviews = twoStarReviews;
        this.threeStarReviews = threeStarReviews;
        this.fourStarReviews = fourStarReviews;
        this.fiveStarReviews = fiveStarReviews;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    // Alias getter for requirement "total review count"
    public long getTotalReviewCount() {
        return totalReviews;
    }

    public long getOneStarReviews() {
        return oneStarReviews;
    }

    public void setOneStarReviews(long oneStarReviews) {
        this.oneStarReviews = oneStarReviews;
    }

    // Alias getters for counts
    public long getCountOf1StarReviews() {
        return oneStarReviews;
    }

    public long getTwoStarReviews() {
        return twoStarReviews;
    }

    public void setTwoStarReviews(long twoStarReviews) {
        this.twoStarReviews = twoStarReviews;
    }

    public long getCountOf2StarReviews() {
        return twoStarReviews;
    }

    public long getThreeStarReviews() {
        return threeStarReviews;
    }

    public void setThreeStarReviews(long threeStarReviews) {
        this.threeStarReviews = threeStarReviews;
    }

    public long getCountOf3StarReviews() {
        return threeStarReviews;
    }

    public long getFourStarReviews() {
        return fourStarReviews;
    }

    public void setFourStarReviews(long fourStarReviews) {
        this.fourStarReviews = fourStarReviews;
    }

    public long getCountOf4StarReviews() {
        return fourStarReviews;
    }

    public long getFiveStarReviews() {
        return fiveStarReviews;
    }

    public void setFiveStarReviews(long fiveStarReviews) {
        this.fiveStarReviews = fiveStarReviews;
    }

    public long getCountOf5StarReviews() {
        return fiveStarReviews;
    }

    public Map<Integer, Long> getRatingDistribution() {
        Map<Integer, Long> dist = new LinkedHashMap<>();
        dist.put(1, oneStarReviews);
        dist.put(2, twoStarReviews);
        dist.put(3, threeStarReviews);
        dist.put(4, fourStarReviews);
        dist.put(5, fiveStarReviews);
        return dist;
    }
}
