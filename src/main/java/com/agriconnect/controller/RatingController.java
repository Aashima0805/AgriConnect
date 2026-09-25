package com.agriconnect.controller;

import com.agriconnect.entity.Notification;
import com.agriconnect.entity.Order;
import com.agriconnect.entity.Rating;
import com.agriconnect.entity.User;
import com.agriconnect.repository.NotificationRepository;
import com.agriconnect.repository.OrderRepository;
import com.agriconnect.repository.RatingRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    private final RatingRepository ratingRepo;
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final NotificationRepository notificationRepo;

    public RatingController(RatingRepository ratingRepo,
                            UserRepository userRepo,
                            OrderRepository orderRepo,
                            NotificationRepository notificationRepo) {
        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.notificationRepo = notificationRepo;
    }

    @GetMapping("/farmer/{id}")
    public ResponseEntity<?> getFarmerRatings(@PathVariable Long id) {
        List<Rating> list = ratingRepo.findByFarmerIdOrderByCreatedAtDesc(id);
        Double avg = ratingRepo.findAverageRatingByFarmerId(id);
        long count = ratingRepo.countRatingsByFarmerId(id);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("farmerId", id);
        res.put("averageRating", avg != null ? Math.round(avg * 10.0) / 10.0 : 5.0);
        res.put("totalRatings", count);
        res.put("reviews", list);

        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<?> addRating(@RequestBody Map<String, Object> req) {
        try {
            Long customerId = Long.valueOf(req.get("customerId").toString());
            Long farmerId = Long.valueOf(req.get("farmerId").toString());
            Long orderId = req.get("orderId") != null ? Long.valueOf(req.get("orderId").toString()) : null;

            int productQuality = Integer.parseInt(req.get("productQuality").toString());
            int deliveryExperience = Integer.parseInt(req.get("deliveryExperience").toString());
            String comment = (String) req.get("comment");

            User customer = userRepo.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            User farmer = userRepo.findById(farmerId)
                    .orElseThrow(() -> new IllegalArgumentException("Farmer not found"));

            // Check if order exists and is delivered
            if (orderId != null) {
                Order order = orderRepo.findById(orderId).orElse(null);
                if (order != null && order.getStatus() != Order.OrderStatus.DELIVERED) {
                    return ResponseEntity.badRequest().body(Map.of("error", "You can only rate a farmer after the order is DELIVERED."));
                }
                if (ratingRepo.findByCustomerIdAndFarmerIdAndOrderId(customerId, farmerId, orderId).isPresent()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "You have already reviewed this purchase order."));
                }
            }

            Rating rating = new Rating();
            rating.setCustomer(customer);
            rating.setFarmer(farmer);
            rating.setOrderId(orderId);
            rating.setProductQuality(Math.max(1, Math.min(5, productQuality)));
            rating.setDeliveryExperience(Math.max(1, Math.min(5, deliveryExperience)));
            rating.setComment(comment);

            Rating saved = ratingRepo.save(rating);

            notificationRepo.save(new Notification(farmer, "New Customer Review!",
                    customer.getName() + " left a " + saved.getAverageScore() + "-star rating on order #" + (orderId != null ? orderId : "direct")));

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Thank you! Your rating and review have been submitted.",
                    "ratingId", saved.getId(),
                    "score", saved.getAverageScore()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to submit rating: " + e.getMessage()));
        }
    }
}
