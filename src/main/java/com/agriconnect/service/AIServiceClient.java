package com.agriconnect.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Component
public class AIServiceClient {

    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> predictDisease(MultipartFile file, String crop) throws IOException {
        String url = aiServiceUrl + "/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // Wrap MultipartFile bytes
        ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() != null ? file.getOriginalFilename() : "leaf.jpg";
            }
        };

        body.add("image", contentsAsResource);
        if (crop != null && !crop.isBlank()) {
            body.add("crop", crop);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), Map.class);
            } else {
                throw new RuntimeException("AI Service returned status: " + response.getStatusCode());
            }
        } catch (ResourceAccessException ex) {
            // Service connection refused or offline
            throw new IllegalStateException("The Python AI Disease Detection Service is currently unavailable. " +
                    "Please start the AI microservice ('python ai-service/app.py' on port 5000) and retry.", ex);
        } catch (Exception ex) {
            if (ex.getCause() instanceof java.net.ConnectException || ex instanceof IllegalStateException) {
                throw new IllegalStateException("The Python AI Disease Detection Service is currently unavailable. " +
                        "Please start the AI microservice ('python ai-service/app.py' on port 5000) and retry.", ex);
            }
            throw new RuntimeException("AI inference failed: " + ex.getMessage(), ex);
        }
    }
}
