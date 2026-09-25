package com.agriconnect.controller;

import com.agriconnect.dto.FarmDetailsDto;
import com.agriconnect.repository.*;
import com.agriconnect.service.FarmService;
import com.agriconnect.service.FertilizerService;
import com.agriconnect.service.MarketPriceService;
import com.agriconnect.service.WeatherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FarmController {

    private final FarmService farmService;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final EquipmentRepository equipmentRepo;
    private final GrievanceRepository grievanceRepo;
    private final RatingRepository ratingRepo;
    private final DonationRepository donationRepo;
    private final WeatherService weatherService;
    private final FertilizerService fertilizerService;
    private final MarketPriceService marketPriceService;

    public FarmController(FarmService farmService,
                          ProductRepository productRepo,
                          OrderRepository orderRepo,
                          EquipmentRepository equipmentRepo,
                          GrievanceRepository grievanceRepo,
                          RatingRepository ratingRepo,
                          DonationRepository donationRepo,
                          WeatherService weatherService,
                          FertilizerService fertilizerService,
                          MarketPriceService marketPriceService) {
        this.farmService = farmService;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.equipmentRepo = equipmentRepo;
        this.grievanceRepo = grievanceRepo;
        this.ratingRepo = ratingRepo;
        this.donationRepo = donationRepo;
        this.weatherService = weatherService;
        this.fertilizerService = fertilizerService;
        this.marketPriceService = marketPriceService;
    }

    @GetMapping("/farms/{farmerId}")
    public ResponseEntity<?> getFarm(@PathVariable Long farmerId) {
        return farmService.getFarmDetails(farmerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(new FarmDetailsDto()));
    }

    @PutMapping("/farms/{farmerId}")
    public ResponseEntity<?> saveOrUpdateFarm(@PathVariable Long farmerId, @Valid @RequestBody FarmDetailsDto details) {
        try {
            FarmDetailsDto saved = farmService.saveOrUpdateFarm(farmerId, details);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/farmers/summary/{farmerId}")
    public ResponseEntity<?> getFarmerSummary(@PathVariable Long farmerId) {
        Map<String, Object> summary = new LinkedHashMap<>();

        long totalProducts = productRepo.countByFarmerId(farmerId);
        long totalOrders = orderRepo.countOrdersByFarmerId(farmerId);
        long pendingOrders = orderRepo.countPendingOrdersByFarmerId(farmerId);
        long equipmentCount = equipmentRepo.countByOwnerId(farmerId);
        long grievanceCount = grievanceRepo.countByFarmerId(farmerId);
        Double avgRating = ratingRepo.findAverageRatingByFarmerId(farmerId);
        long totalRatings = ratingRepo.countRatingsByFarmerId(farmerId);
        BigDecimal donations = donationRepo.sumDonationsForFarmer(farmerId);

        summary.put("totalProducts", totalProducts);
        summary.put("totalOrders", totalOrders);
        summary.put("pendingOrders", pendingOrders);
        summary.put("equipmentCount", equipmentCount);
        summary.put("grievanceCount", grievanceCount);
        summary.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 5.0);
        summary.put("totalRatings", totalRatings);
        summary.put("donationsReceived", donations != null ? donations : BigDecimal.ZERO);

        return ResponseEntity.ok(summary);
    }
}
