package com.agriconnect.controller;

import com.agriconnect.dto.CropDiseasePredictionResponse;
import com.agriconnect.service.CropDiseaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/crop-disease", "/api/crop"})
@CrossOrigin(origins = "*")
public class CropDiseaseController {

    private final CropDiseaseService cropDiseaseService;

    public CropDiseaseController(CropDiseaseService cropDiseaseService) {
        this.cropDiseaseService = cropDiseaseService;
    }

    @PostMapping(value = {"/predict", "/diagnose"}, consumes = {"multipart/form-data"})
    public ResponseEntity<CropDiseasePredictionResponse> predictDisease(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "crop", required = false, defaultValue = "Rice") String crop) {

        CropDiseasePredictionResponse response = cropDiseaseService.analyzeCropDisease(image, crop);
        return ResponseEntity.ok(response);
    }
}
