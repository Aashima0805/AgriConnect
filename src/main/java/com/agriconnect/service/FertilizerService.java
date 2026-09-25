package com.agriconnect.service;

import com.agriconnect.dto.FertilizerRequest;
import com.agriconnect.dto.FertilizerResponse;
import com.agriconnect.dto.FertilizerResponse.FertilizerPlanItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FertilizerService {

    public FertilizerResponse getRecommendation(FertilizerRequest req) {
        if (req == null || req.getCropName() == null || req.getCropName().isBlank()) {
            throw new IllegalArgumentException("Crop name is required for fertilizer recommendation.");
        }

        String crop = req.getCropName().toLowerCase().trim();
        String soil = (req.getSoilType() != null && !req.getSoilType().isBlank()) ? req.getSoilType().toLowerCase().trim() : "alluvial";
        double area = (req.getLandArea() != null && req.getLandArea() > 0) ? req.getLandArea() : 1.0;
        String unit = (req.getLandUnit() != null && !req.getLandUnit().isBlank()) ? req.getLandUnit().trim() : "acres";
        String stage = (req.getStage() != null && !req.getStage().isBlank()) ? req.getStage().trim() : "Basal / Vegetative";

        FertilizerResponse response = new FertilizerResponse();
        response.setCrop(req.getCropName().trim());
        response.setSoilType(req.getSoilType() != null ? req.getSoilType().trim() : "Alluvial / Loam");
        response.setLandArea(area);
        response.setLandUnit(unit);
        response.setCropStage(stage);

        List<FertilizerPlanItem> items = new ArrayList<>();
        String primaryFocus;
        String guidance;
        String precautions;

        // Rule-based agronomic logic
        if (crop.contains("rice") || crop.contains("paddy")) {
            primaryFocus = "High Nitrogen & Balanced Phosphorus-Potassium (NPK Ratio 100:50:50 kg/ha equivalent)";
            items.add(new FertilizerPlanItem("Urea (46% N)", "Nitrogen (N)", Math.round(55 * area) + " kg",
                    "Split: 1/3 Basal at transplanting, 1/3 at Active Tillering (20-25 DAT), 1/3 at Panicle Initiation (45 DAT)", "Broadcast evenly"));
            items.add(new FertilizerPlanItem("DAP (18:46:0)", "Phosphorus (P2O5)", Math.round(45 * area) + " kg",
                    "100% applied as basal dose before final soil puddling", "Incorporate into soil"));
            items.add(new FertilizerPlanItem("Muriate of Potash (MOP 60% K2O)", "Potassium (K2O)", Math.round(30 * area) + " kg",
                    "50% at basal puddling, 50% at heading/flowering stage for grain filling", "Broadcast top-dressing"));

            if (soil.contains("clay") || soil.contains("black") || (req.getPh() != null && req.getPh() > 7.8)) {
                items.add(new FertilizerPlanItem("Zinc Sulphate (ZnSO4 21% or 33%)", "Zinc (Micro-nutrient)", Math.round(10 * area) + " kg",
                        "Apply as basal during land preparation to prevent Khaira disease", "Soil incorporation"));
            }

            guidance = "Drain standing water to a shallow depth (1-2 cm) prior to urea application. Re-flood the paddy field after 24 hours to maximize nitrogen absorption and prevent denitrification.";
            precautions = "Do not apply urea under imminent heavy rain. Never mix Zinc Sulphate directly with phosphatic fertilizers (DAP) as it forms insoluble zinc phosphate.";

        } else if (crop.contains("wheat")) {
            primaryFocus = "Balanced NPK (120:60:40 kg/ha equivalent) with strong early Phosphorus";
            items.add(new FertilizerPlanItem("Urea (46% N)", "Nitrogen (N)", Math.round(65 * area) + " kg",
                    "1/2 at sowing, 1/4 at Crown Root Initiation (CRI at 21 DAS), 1/4 at Heading/Booting", "Top-dressing with irrigation"));
            items.add(new FertilizerPlanItem("DAP (18:46:0)", "Phosphorus (P2O5)", Math.round(50 * area) + " kg",
                    "Full dose drilled beneath seed zone at sowing time", "Drill application"));
            items.add(new FertilizerPlanItem("MOP (60% K2O)", "Potassium (K2O)", Math.round(25 * area) + " kg",
                    "Apply full dose at primary tillage/seedbed preparation", "Basal broadcast"));

            guidance = "First top dressing must coincide with the Crown Root Initiation (CRI) irrigation (20-25 days after sowing) for optimal tillering.";
            precautions = "Avoid broadcast urea in hot midday temperatures; apply in the late afternoon followed by irrigation.";

        } else if (crop.contains("tomato") || crop.contains("brinjal") || crop.contains("chilli") || crop.contains("vegetable")) {
            primaryFocus = "High Potassium & Calcium for Fruit Quality & Firmness";
            items.add(new FertilizerPlanItem("Water-Soluble NPK (19:19:19)", "Balanced Macro-nutrients", Math.round(15 * area) + " kg",
                    "Weekly intervals throughout active vegetative and early flowering stages", "Fertigation / Drip application"));
            items.add(new FertilizerPlanItem("Calcium Nitrate + Boron", "Calcium & Boron", Math.round(8 * area) + " kg",
                    "At flowering initiation and fruit set (prevents Blossom End Rot & fruit cracking)", "Foliar spray / Fertigation"));
            items.add(new FertilizerPlanItem("Potassium Nitrate (13:0:45)", "Potassium (K2O)", Math.round(20 * area) + " kg",
                    "Fruit enlargement and ripening stage for fruit color, sugar content, and firmness", "Fertigation / Drip"));
            items.add(new FertilizerPlanItem("Well-rotted FYM / Vermicompost", "Organic Matter", Math.round(250 * area) + " kg",
                    "Incorporate in raised beds 2 weeks before seedling transplanting", "Basal soil mix"));

            guidance = "Use drip fertigation for high nutrient use efficiency. Maintain consistent moisture to assist calcium uptake.";
            precautions = "Avoid excess nitrogen after flowering to prevent flower drop and excessive vegetative vine growth.";

        } else if (crop.contains("maize") || crop.contains("corn")) {
            primaryFocus = "Nitrogen Heavy with Balanced Zinc & Phosphorus";
            items.add(new FertilizerPlanItem("Urea (46% N)", "Nitrogen (N)", Math.round(70 * area) + " kg",
                    "Split: 1/3 at sowing, 1/3 at knee-high stage (30 DAS), 1/3 at tasseling/silking stage", "Side-dressing along rows"));
            items.add(new FertilizerPlanItem("DAP (18:46:0)", "Phosphorus (P2O5)", Math.round(45 * area) + " kg",
                    "Band placement 5 cm below and away from seeds at planting", "Band placement"));
            items.add(new FertilizerPlanItem("MOP (60% K2O)", "Potassium (K2O)", Math.round(25 * area) + " kg",
                    "Apply 100% as basal dose during field preparation", "Basal broadcast"));

            guidance = "Knee-high and tasseling stages are peak nitrogen demand periods; ensure soil moisture when applying urea.";
            precautions = "Do not place urea in direct contact with germinating seeds to avoid seedling burn.";

        } else if (crop.contains("cotton")) {
            primaryFocus = "High Potassium & Nitrogen for Boll Retention and Fiber Quality";
            items.add(new FertilizerPlanItem("Urea (46% N)", "Nitrogen (N)", Math.round(60 * area) + " kg",
                    "Split into 3 doses: squaring stage, flowering stage, and peak boll development", "Side-dressing"));
            items.add(new FertilizerPlanItem("Single Super Phosphate (SSP)", "Phosphorus & Sulphur", Math.round(80 * area) + " kg",
                    "Full basal application at seed sowing", "Basal incorporation"));
            items.add(new FertilizerPlanItem("MOP (60% K2O)", "Potassium (K2O)", Math.round(35 * area) + " kg",
                    "Split: 50% at sowing, 50% at flowering stage to prevent premature leaf reddening", "Soil application"));
            items.add(new FertilizerPlanItem("Magnesium Sulphate (MgSO4)", "Magnesium & Sulphur", Math.round(15 * area) + " kg",
                    "At peak squaring to prevent leaf reddening and boll drop", "Foliar spray / Soil application"));

            guidance = "Cotton is sensitive to potassium and magnesium deficiency during boll development; split potash applications prevent square drop.";
            precautions = "Excessive late-season nitrogen causes lush vegetative growth and attracts sucking pests (whiteflies, jassids).";

        } else if (crop.contains("groundnut") || crop.contains("peanut") || crop.contains("soybean") || crop.contains("pulse") || crop.contains("gram")) {
            primaryFocus = "Phosphorus, Calcium & Rhizobium Biological Nitrogen Fixation";
            items.add(new FertilizerPlanItem("DAP (18:46:0)", "Starter Nitrogen & High Phosphorus", Math.round(30 * area) + " kg",
                    "Full basal dose drilled at sowing time to stimulate root nodulation", "Drill sowing"));
            items.add(new FertilizerPlanItem("Gypsum (Calcium Sulphate - Ca: 24%, S: 18%)", "Calcium & Sulphur", Math.round(150 * area) + " kg",
                    "Split: 50% at sowing, 50% at flowering/pegging stage (40 DAS) for pod development", "Band application near root zone"));
            items.add(new FertilizerPlanItem("Rhizobium & PSB Bio-fertilizers", "Bio-inoculants", Math.round(2 * area) + " kg",
                    "Seed treatment before sowing", "Seed coating with jaggery slurry"));

            guidance = "Gypsum at pegging stage is critical for groundnut pod filling and preventing empty shell syndrome (pops).";
            precautions = "Avoid heavy nitrogen fertilizer as it suppresses natural root nodule nitrogen fixation.";

        } else {
            primaryFocus = "Standard Balanced Crop Nutrition (NPK 20:20:20 + Organic Manure)";
            items.add(new FertilizerPlanItem("Complex NPK (20:20:20)", "Balanced Macro-nutrients", Math.round(40 * area) + " kg",
                    "Split equally between early vegetative and reproductive development phases", "Soil application"));
            items.add(new FertilizerPlanItem("Decomposed Farmyard Manure (FYM)", "Soil Organic Carbon", Math.round(400 * area) + " kg",
                    "Apply during initial land plowing and field leveling", "Deep soil incorporation"));

            guidance = "Distribute nutrients aligned with the crop growth cycle. Ensure adequate soil moisture at every fertilizer application.";
            precautions = "Store synthetic fertilizers in moisture-proof containers on raised wooden pallets away from direct sun.";
        }

        // Adjust for specific soil test NPK values if user provided them
        if (req.getNitrogen() != null && req.getNitrogen() > 250) {
            guidance += " [Soil Note: High baseline soil Nitrogen detected; consider reducing urea dose by 15-20%].";
        }
        if (req.getPhosphorus() != null && req.getPhosphorus() < 15) {
            guidance += " [Soil Note: Low baseline soil Phosphorus detected; ensure full phosphatic basal dose is placed directly in root zone].";
        }
        if (req.getPotassium() != null && req.getPotassium() < 120) {
            guidance += " [Soil Note: Low baseline Potassium; foliar spray of 1% KNO3 recommended during reproductive phase].";
        }

        response.setPrimaryNutrientFocus(primaryFocus);
        response.setFertilizerSchedule(items);
        response.setApplicationGuidance(guidance);
        response.setPrecautions(precautions);

        return response;
    }

    // Overload for backward compatibility with existing query param callers
    public FertilizerResponse recommend(String crop, String soilType, Double landArea, String irrigationType, String stage) {
        FertilizerRequest req = new FertilizerRequest();
        req.setCropName(crop != null && !crop.isBlank() ? crop : "Rice");
        req.setSoilType(soilType);
        req.setLandArea(landArea != null ? landArea : 1.0);
        req.setIrrigationType(irrigationType);
        req.setStage(stage);
        return getRecommendation(req);
    }
}
