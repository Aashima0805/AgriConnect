package com.agriconnect.dto;

import java.util.List;
import java.util.Map;

public class CropDiseasePredictionResponse {

    private String status; // "success", "low_confidence", "error"
    private String crop;
    private String disease;
    private Double confidence;
    private String confidencePercentage;
    private String aiModel = "Ultralytics YOLO11n (best.pt)";
    private List<Map<String, Object>> detections;
    private String message;

    // Static Domain Agricultural Advisory (Informational agronomy guidance separate from model prediction)
    private String symptoms;
    private String recommendation;
    private String prevention;
    private String generalPrecautions;
    private String guidanceDisclaimer = "Prevention and management recommendations are static agronomic domain guidance based on standard agricultural extension protocols, provided separately from the YOLO vision detection model.";

    public CropDiseasePredictionResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
        if (confidence != null) {
            this.confidencePercentage = Math.round(confidence * 100) + "%";
        }
    }

    public String getConfidencePercentage() {
        return confidencePercentage;
    }

    public void setConfidencePercentage(String confidencePercentage) {
        this.confidencePercentage = confidencePercentage;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public List<Map<String, Object>> getDetections() {
        return detections;
    }

    public void setDetections(List<Map<String, Object>> detections) {
        this.detections = detections;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getPrevention() {
        return prevention;
    }

    public void setPrevention(String prevention) {
        this.prevention = prevention;
    }

    public String getGeneralPrecautions() {
        return generalPrecautions;
    }

    public void setGeneralPrecautions(String generalPrecautions) {
        this.generalPrecautions = generalPrecautions;
    }

    public String getGuidanceDisclaimer() {
        return guidanceDisclaimer;
    }

    public void setGuidanceDisclaimer(String guidanceDisclaimer) {
        this.guidanceDisclaimer = guidanceDisclaimer;
    }
}
