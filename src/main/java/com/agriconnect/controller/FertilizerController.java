package com.agriconnect.controller;

import com.agriconnect.dto.FertilizerRequest;
import com.agriconnect.dto.FertilizerResponse;
import com.agriconnect.service.FertilizerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fertilizer")
@CrossOrigin(origins = "*")
public class FertilizerController {

    private final FertilizerService fertilizerService;

    public FertilizerController(FertilizerService fertilizerService) {
        this.fertilizerService = fertilizerService;
    }

    @PostMapping("/recommendation")
    public ResponseEntity<?> getRecommendation(@Valid @RequestBody FertilizerRequest request) {
        try {
            FertilizerResponse res = fertilizerService.getRecommendation(request);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/recommendation")
    public ResponseEntity<?> getRecommendationGet(@RequestParam(required = false) String crop,
                                                 @RequestParam(required = false) String soilType,
                                                 @RequestParam(required = false) Double landArea,
                                                 @RequestParam(required = false) String irrigationType,
                                                 @RequestParam(required = false) String stage) {
        try {
            FertilizerResponse res = fertilizerService.recommend(crop, soilType, landArea, irrigationType, stage);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
