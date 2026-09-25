package com.agriconnect.service;

import com.agriconnect.dto.CropDiseasePredictionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CropDiseaseService {

    private final AIServiceClient aiServiceClient;

    public CropDiseaseService(AIServiceClient aiServiceClient) {
        this.aiServiceClient = aiServiceClient;
    }

    @SuppressWarnings("unchecked")
    public CropDiseasePredictionResponse analyzeCropDisease(MultipartFile file, String crop) {
        CropDiseasePredictionResponse response = new CropDiseasePredictionResponse();
        response.setCrop(crop != null && !crop.isBlank() ? crop : "Rice");

        // 1. Image Validation
        if (file == null || file.isEmpty()) {
            response.setStatus("error");
            response.setMessage("Please select a valid image file to analyze.");
            return response;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageExtension(originalFilename)) {
            response.setStatus("error");
            response.setMessage("Invalid file format. Supported formats are JPG, JPEG, and PNG.");
            return response;
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            response.setStatus("error");
            response.setMessage("Image size exceeds the 10MB limit. Please upload a smaller image.");
            return response;
        }

        // 2. Call Python AI Microservice (Zero mock/random fallback)
        try {
            Map<String, Object> aiResult = aiServiceClient.predictDisease(file, response.getCrop());
            String status = (String) aiResult.getOrDefault("status", "error");

            if ("low_confidence".equalsIgnoreCase(status)) {
                response.setStatus("low_confidence");
                Number confNum = (Number) aiResult.get("confidence");
                if (confNum != null) response.setConfidence(confNum.doubleValue());
                response.setMessage((String) aiResult.getOrDefault("message",
                        "The uploaded image could not be classified reliably. Please upload a clear photo of the leaf."));
                Object detectionsObj = aiResult.get("detections");
                if (detectionsObj instanceof List<?>) {
                    response.setDetections((List<Map<String, Object>>) detectionsObj);
                } else {
                    response.setDetections(Collections.emptyList());
                }
                return response;
            }

            if ("success".equalsIgnoreCase(status)) {
                response.setStatus("success");
                String disease = (String) aiResult.get("disease");
                Number confNum = (Number) aiResult.get("confidence");
                double confidence = confNum != null ? confNum.doubleValue() : 0.0;

                response.setDisease(disease);
                response.setConfidence(confidence);
                Object detectionsObj = aiResult.get("detections");
                if (detectionsObj instanceof List<?>) {
                    response.setDetections((List<Map<String, Object>>) detectionsObj);
                } else {
                    response.setDetections(Collections.emptyList());
                }

                // Map genuinely predicted disease class to static domain information
                populateDiseaseAdvisory(response, disease);
                return response;
            }

            response.setStatus("error");
            response.setMessage((String) aiResult.getOrDefault("message", "AI inference returned an unexpected error."));
            return response;

        } catch (IllegalStateException ex) {
            // Explicit error when AI service is offline
            response.setStatus("error");
            response.setMessage(ex.getMessage());
            return response;
        } catch (Exception ex) {
            response.setStatus("error");
            response.setMessage("Failed to process disease detection: " + ex.getMessage());
            return response;
        }
    }

    private boolean isValidImageExtension(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
    }

    /**
     * Static informational guidance for the 6 trained rice leaf disease classes.
     * Note: This advisory is static domain knowledge, not AI prediction.
     */
    private void populateDiseaseAdvisory(CropDiseasePredictionResponse response, String disease) {
        if (disease == null) return;

        switch (disease.trim()) {
            case "Bacterial Leaf Blight" -> {
                response.setSymptoms("Water-soaked to yellowish-white wavy lesions starting from leaf margins towards the base; leaves turn grayish-white and wilt rapidly under warm, humid conditions.");
                response.setRecommendation("Drain standing water from the field. Avoid nitrogen top-dressing during active infection. Apply copper hydroxide (2g/L) or approved bactericide per local extension advice.");
                response.setPrevention("Cultivate resistant varieties, practice balanced NPK fertilization with split potassium, and avoid field-to-field irrigation from infested plots.");
                response.setGeneralPrecautions("Disinfect farming tools and avoid moving through fields while foliage is wet to minimize bacterial spread.");
            }
            case "Brown Spot" -> {
                response.setSymptoms("Oval or circular dark-brown spots with grayish centers and yellow halos on leaves, leaf sheaths, and glumes.");
                response.setRecommendation("Apply protective foliar fungicides like Mancozeb (2g/L) or Edifenphos at early detection. Supplement soil with potassium and silicon.");
                response.setPrevention("Treat seeds with fungicide or hot water soak (52-54°C for 10-15 mins) prior to sowing. Ensure balanced soil nutrition and avoid soil moisture stress.");
                response.setGeneralPrecautions("Burn or incorporate infected crop residues after harvest to eliminate overwintering spores.");
            }
            case "Healthy" -> {
                response.setSymptoms("Normal green leaf texture without chlorosis, necrotic spotting, or blight lesions.");
                response.setRecommendation("The crop is in healthy condition. Maintain scheduled irrigation and balanced nutrient management.");
                response.setPrevention("Continue routine crop scouting, adhere to recommended planting density, and follow integrated crop management practices.");
                response.setGeneralPrecautions("Keep bunds and irrigation canals weed-free to prevent harboring insect vectors.");
            }
            case "Leaf Blast" -> {
                response.setSymptoms("Diamond or spindle-shaped lesions with gray-white centers and dark brown borders; spots enlarge and coalesce causing extensive leaf desiccation (blast).");
                response.setRecommendation("Spray systemic fungicide such as Tricyclazole 75% WP (0.6g/L) or Isoprothiolane immediately at first appearance. Suspend nitrogen top-dressing.");
                response.setPrevention("Avoid late planting and high nursery seeding density. Maintain continuous shallow flooding in paddy fields.");
                response.setGeneralPrecautions("Do not allow fields to undergo severe drought in vegetative stages, which exacerbates blast susceptibility.");
            }
            case "Leaf scald" -> {
                response.setSymptoms("Zonate lesions with alternating light and dark brown bands resembling chevron markings, expanding inward from leaf tips.");
                response.setRecommendation("Apply copper oxychloride (2.5g/L) or carbendazim upon noticing rapid lesion expansion.");
                response.setPrevention("Use certified disease-free seeds, avoid excessive nitrogen applications, and maintain proper field spacing.");
                response.setGeneralPrecautions("Ensure good air circulation across the field canopy.");
            }
            case "Narrow Brown Spot" -> {
                response.setSymptoms("Narrow, short, linear reddish-brown lesions running strictly parallel between leaf veins, usually emerging during heading to maturity stages.");
                response.setRecommendation("Apply protective foliar fungicide (e.g. Propiconazole) if lesions exceed 10% canopy coverage before flowering.");
                response.setPrevention("Select resistant rice cultivars; maintain adequate potassium fertility according to soil test recommendations.");
                response.setGeneralPrecautions("Harvest promptly at grain maturity to prevent glume and grain infection.");
            }
            default -> {
                response.setSymptoms("Detected pattern: " + disease);
                response.setRecommendation("Consult local agricultural university or Krishi Vigyan Kendra (KVK) with leaf samples.");
                response.setPrevention("Follow standard integrated pest and disease management (IPM) guidelines.");
                response.setGeneralPrecautions("Maintain field hygiene and sanitation.");
            }
        }
    }
}
