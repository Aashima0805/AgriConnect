package com.agriconnect.service;

import com.agriconnect.dto.ProductReviewSummaryDto;
import com.agriconnect.dto.ReviewDto;
import com.agriconnect.dto.ReviewRequest;
import com.agriconnect.entity.Product;
import com.agriconnect.entity.Review;
import com.agriconnect.entity.User;
import com.agriconnect.repository.OrderItemRepository;
import com.agriconnect.repository.ProductRepository;
import com.agriconnect.repository.ReviewRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final OrderItemRepository orderItemRepo;

    public ReviewService(ReviewRepository reviewRepo,
                         ProductRepository productRepo,
                         UserRepository userRepo,
                         OrderItemRepository orderItemRepo) {
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @Transactional
    public ReviewDto createReview(ReviewRequest req, Long requestingUserId) {
        if (req == null) {
            throw new IllegalArgumentException("Review request body cannot be empty.");
        }
        if (req.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        if (req.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }
        if (req.getRating() == null) {
            throw new IllegalArgumentException("Rating is required.");
        }
        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be an integer between 1 and 5.");
        }
        if (req.getComment() != null && req.getComment().length() > 2000) {
            throw new IllegalArgumentException("Review comment cannot exceed 2000 characters.");
        }

        // Authorization check: requesting user must match customer ID
        if (requestingUserId != null && !requestingUserId.equals(req.getCustomerId())) {
            throw new SecurityException("Unauthorized: You can only submit reviews for your own customer account.");
        }

        User customer = userRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + req.getCustomerId()));

        if (customer.getRole() != User.Role.CUSTOMER && customer.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("Only customers are authorized to review products.");
        }

        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + req.getProductId()));

        // Verification: customer must have purchased the product through an order
        boolean purchased = orderItemRepo.hasCustomerPurchasedProduct(customer.getId(), product.getId())
                || orderItemRepo.existsByOrderCustomerIdAndProductId(customer.getId(), product.getId());

        if (!purchased) {
            throw new IllegalArgumentException("You can only review products that you have purchased through a valid order.");
        }

        // Prevent duplicate reviews for the same customer and product
        if (reviewRepo.existsByCustomerIdAndProductId(customer.getId(), product.getId())) {
            throw new IllegalArgumentException("You have already reviewed this product. Please use the update review endpoint to modify your rating.");
        }

        Review review = new Review(customer, product, req.getRating(), req.getComment() != null ? req.getComment().trim() : null);
        Review saved = reviewRepo.save(review);

        return ReviewDto.fromEntity(saved);
    }

    public ReviewDto getReviewById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Review ID is required.");
        }
        Review r = reviewRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
        return ReviewDto.fromEntity(r);
    }

    public List<ReviewDto> getReviewsByProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }
        if (!productRepo.existsById(productId)) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
        return reviewRepo.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getReviewsByCustomer(Long customerId, Long requestingUserId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        if (requestingUserId != null && !customerId.equals(requestingUserId)) {
            throw new SecurityException("Unauthorized: You can only view reviews for your own customer account.");
        }
        if (!userRepo.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        return reviewRepo.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDto updateReview(Long reviewId, Integer rating, String comment, Long requestingUserId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID is required.");
        }
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        if (requestingUserId != null && !review.getCustomer().getId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized: You can only modify your own reviews.");
        }

        if (rating != null) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5.");
            }
            review.setRating(rating);
        }

        if (comment != null) {
            if (comment.length() > 2000) {
                throw new IllegalArgumentException("Review comment cannot exceed 2000 characters.");
            }
            review.setComment(comment.trim());
        }

        review.setUpdatedAt(LocalDateTime.now());
        Review saved = reviewRepo.save(review);
        return ReviewDto.fromEntity(saved);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long requestingUserId, boolean isAdmin) {
        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID is required.");
        }
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        if (!isAdmin && requestingUserId != null && !review.getCustomer().getId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized: You can only delete your own reviews.");
        }

        reviewRepo.delete(review);
    }

    public ProductReviewSummaryDto getProductReviewSummary(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        Double avg = reviewRepo.findAverageRatingByProductId(productId);
        long total = reviewRepo.countByProductId(productId);
        long c1 = reviewRepo.countByProductIdAndRating(productId, 1);
        long c2 = reviewRepo.countByProductIdAndRating(productId, 2);
        long c3 = reviewRepo.countByProductIdAndRating(productId, 3);
        long c4 = reviewRepo.countByProductIdAndRating(productId, 4);
        long c5 = reviewRepo.countByProductIdAndRating(productId, 5);

        double roundedAvg = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
        return new ProductReviewSummaryDto(productId, product.getName(), roundedAvg, total, c1, c2, c3, c4, c5);
    }

    public List<ReviewDto> getAllReviews() {
        return reviewRepo.findAllByOrderByCreatedAtDesc().stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }
}
