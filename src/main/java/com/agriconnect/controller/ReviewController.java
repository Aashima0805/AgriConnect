package com.agriconnect.controller;

import com.agriconnect.dto.ProductReviewSummaryDto;
import com.agriconnect.dto.ReviewDto;
import com.agriconnect.dto.ReviewRequest;
import com.agriconnect.entity.User;
import com.agriconnect.repository.UserRepository;
import com.agriconnect.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepo;

    public ReviewController(ReviewService reviewService, UserRepository userRepo) {
        this.reviewService = reviewService;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest req,
                                          @RequestParam(required = false) Long customerId,
                                          @RequestParam(required = false) Long userId,
                                          @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
                                          Principal principal) {
        try {
            Long authUserId = resolveUserId(customerId, userId, headerUserId, principal);
            ReviewDto created = reviewService.createReview(req, authUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to submit review: " + e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Long productId) {
        try {
            List<ReviewDto> reviews = reviewService.getReviewsByProduct(productId);
            return ResponseEntity.ok(reviews);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<?> getProductReviewSummary(@PathVariable Long productId) {
        try {
            ProductReviewSummaryDto summary = reviewService.getProductReviewSummary(productId);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerReviews(@PathVariable Long customerId,
                                                @RequestParam(required = false) Long userId,
                                                @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
                                                Principal principal) {
        try {
            Long authUserId = resolveUserId(null, userId, headerUserId, principal);
            List<ReviewDto> reviews = reviewService.getReviewsByCustomer(customerId, authUserId);
            return ResponseEntity.ok(reviews);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        try {
            ReviewDto review = reviewService.getReviewById(id);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id,
                                          @RequestBody Map<String, Object> req,
                                          @RequestParam(required = false) Long customerId,
                                          @RequestParam(required = false) Long userId,
                                          @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
                                          Principal principal) {
        try {
            Long authUserId = resolveUserId(customerId, userId, headerUserId, principal);
            Integer rating = req.containsKey("rating") && req.get("rating") != null
                    ? Integer.valueOf(req.get("rating").toString())
                    : null;
            String comment = req.containsKey("comment") && req.get("comment") != null
                    ? req.get("comment").toString()
                    : null;

            ReviewDto updated = reviewService.updateReview(id, rating, comment, authUserId);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id,
                                          @RequestParam(required = false) Long customerId,
                                          @RequestParam(required = false) Long userId,
                                          @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
                                          Principal principal) {
        try {
            Long authUserId = resolveUserId(customerId, userId, headerUserId, principal);
            boolean isAdmin = checkIsAdmin(authUserId, principal);
            reviewService.deleteReview(id, authUserId, isAdmin);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Review deleted successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    private Long resolveUserId(Long customerId, Long userId, Long headerUserId, Principal principal) {
        if (customerId != null) return customerId;
        if (userId != null) return userId;
        if (headerUserId != null) return headerUserId;
        if (principal != null) {
            String username = principal.getName();
            if (username != null) {
                try {
                    return Long.valueOf(username);
                } catch (NumberFormatException ignored) {}
                return userRepo.findByEmail(username.trim().toLowerCase())
                        .map(User::getId)
                        .orElse(null);
            }
        }
        return null;
    }

    private boolean checkIsAdmin(Long authUserId, Principal principal) {
        if (authUserId != null) {
            return userRepo.findById(authUserId)
                    .map(u -> u.getRole() == User.Role.ADMIN)
                    .orElse(false);
        }
        if (principal != null) {
            String username = principal.getName();
            if (username != null) {
                return userRepo.findByEmail(username.trim().toLowerCase())
                        .map(u -> u.getRole() == User.Role.ADMIN)
                        .orElse(false);
            }
        }
        return false;
    }
}
