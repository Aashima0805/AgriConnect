package com.agriconnect.dto;

import java.util.ArrayList;
import java.util.List;

public class FertilizerResponse {

    private String crop;
    private String soilType;
    private Double landArea;
    private String landUnit;
    private String cropStage;
    private String primaryNutrientFocus;
    private List<FertilizerPlanItem> fertilizerSchedule = new ArrayList<>();
    private String applicationGuidance;
    private String precautions;
    private String recommendationType = "Rule-Based Agronomic Logic (Standard Agricultural Best Practices)";
    private String disclaimer = "This is a rule-based agronomic decision support recommendation based on ICAR/Agricultural University crop guidelines, not an AI/ML black-box prediction. For precision farming, verify against a localized soil test report.";

    public static class FertilizerPlanItem {
        private String fertilizerName;
        private String nutrientFocus; // e.g. "Nitrogen (N)", "Phosphorus (P2O5)", "Potassium (K2O)", "Micro-nutrients (Zn, B)"
        private String dosage;
        private String timing;
        private String method;

        public FertilizerPlanItem() {
        }

        public FertilizerPlanItem(String fertilizerName, String nutrientFocus, String dosage, String timing, String method) {
            this.fertilizerName = fertilizerName;
            this.nutrientFocus = nutrientFocus;
            this.dosage = dosage;
            this.timing = timing;
            this.method = method;
        }

        public String getFertilizerName() {
            return fertilizerName;
        }

        public void setFertilizerName(String fertilizerName) {
            this.fertilizerName = fertilizerName;
        }

        public String getNutrientFocus() {
            return nutrientFocus;
        }

        public void setNutrientFocus(String nutrientFocus) {
            this.nutrientFocus = nutrientFocus;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getTiming() {
            return timing;
        }

        public void setTiming(String timing) {
            this.timing = timing;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }

    public FertilizerResponse() {
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public Double getLandArea() {
        return landArea;
    }

    public void setLandArea(Double landArea) {
        this.landArea = landArea;
    }

    public String getLandUnit() {
        return landUnit;
    }

    public void setLandUnit(String landUnit) {
        this.landUnit = landUnit;
    }

    public String getCropStage() {
        return cropStage;
    }

    public void setCropStage(String cropStage) {
        this.cropStage = cropStage;
    }

    public String getPrimaryNutrientFocus() {
        return primaryNutrientFocus;
    }

    public void setPrimaryNutrientFocus(String primaryNutrientFocus) {
        this.primaryNutrientFocus = primaryNutrientFocus;
    }

    public List<FertilizerPlanItem> getFertilizerSchedule() {
        return fertilizerSchedule;
    }

    public void setFertilizerSchedule(List<FertilizerPlanItem> fertilizerSchedule) {
        this.fertilizerSchedule = fertilizerSchedule;
    }

    public String getApplicationGuidance() {
        return applicationGuidance;
    }

    public void setApplicationGuidance(String applicationGuidance) {
        this.applicationGuidance = applicationGuidance;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
