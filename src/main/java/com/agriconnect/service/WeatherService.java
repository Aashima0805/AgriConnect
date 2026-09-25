package com.agriconnect.service;

import com.agriconnect.dto.WeatherDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    @Value("${weather.api.key:}")
    private String apiKey;

    @Value("${weather.api.base-url:https://api.openweathermap.org/data/2.5/weather}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public WeatherDto getWeather(Double lat, Double lon, String locationName) {
        if (apiKey == null || apiKey.isBlank()) {
            return WeatherDto.error("OpenWeatherMap API key is not configured. Please set 'weather.api.key' in application.properties or environment variables to retrieve live weather data.");
        }

        try {
            String queryUrl;
            if (lat != null && lon != null) {
                queryUrl = String.format("%s?lat=%f&lon=%f&appid=%s&units=metric",
                        baseUrl, lat, lon, apiKey);
            } else if (locationName != null && !locationName.isBlank()) {
                queryUrl = String.format("%s?q=%s&appid=%s&units=metric",
                        baseUrl, URLEncoder.encode(locationName.trim(), StandardCharsets.UTF_8), apiKey);
            } else {
                return WeatherDto.error("Location parameter is required. Please provide either latitude/longitude or a location name.");
            }

            HttpRequest req = HttpRequest.newBuilder(URI.create(queryUrl))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("OpenWeatherMap API returned status {}: {}", response.statusCode(), response.body());
                if (response.statusCode() == 401) {
                    return WeatherDto.error("Invalid or inactive OpenWeatherMap API key.");
                } else if (response.statusCode() == 404) {
                    return WeatherDto.error("Location not found on OpenWeatherMap: " + (locationName != null ? locationName : "(" + lat + "," + lon + ")"));
                }
                return WeatherDto.error("OpenWeatherMap service returned HTTP " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (!root.has("main")) {
                return WeatherDto.error("Unexpected response structure from OpenWeatherMap.");
            }

            String resolvedLocation = root.path("name").asText(locationName != null ? locationName : "Target Farm");
            double temp = root.path("main").path("temp").asDouble();
            double feelsLike = root.path("main").path("feels_like").asDouble();
            int humidity = root.path("main").path("humidity").asInt();
            int pressure = root.path("main").path("pressure").asInt();
            double windSpeed = root.path("wind").path("speed").asDouble();

            String condition = "Clear";
            String description = "clear sky";
            if (root.has("weather") && root.path("weather").isArray() && !root.path("weather").isEmpty()) {
                condition = root.path("weather").path(0).path("main").asText("Clear");
                description = root.path("weather").path(0).path("description").asText("clear sky");
            }

            String advisory = generateAgriculturalAdvisory(condition, humidity, windSpeed);

            return WeatherDto.success(
                    resolvedLocation,
                    temp,
                    feelsLike,
                    humidity,
                    windSpeed,
                    condition,
                    description,
                    pressure,
                    advisory
            );

        } catch (Exception e) {
            log.error("Failed to fetch weather data: {}", e.getMessage());
            return WeatherDto.error("Failed to retrieve weather data from external service: " + e.getMessage());
        }
    }

    private String generateAgriculturalAdvisory(String condition, int humidity, double windSpeed) {
        if (condition.equalsIgnoreCase("Rain") || condition.equalsIgnoreCase("Thunderstorm") || condition.equalsIgnoreCase("Drizzle")) {
            return "Precipitation alert: Ensure drainage channels in low-lying crop beds are clear. Postpone all chemical sprayings and fertilizer top-dressing until foliage is dry.";
        }
        if (windSpeed > 7.0) {
            return "High wind speed alert (>7 m/s): Avoid foliar fertilizer and pesticide application due to excessive drift and poor droplet retention.";
        }
        if (humidity > 85) {
            return "Elevated humidity: High risk of foliar fungal pathogens (e.g. Blast, Sheath Blight). Inspect crop canopy closely for early lesions.";
        }
        return "Optimal weather: Atmospheric conditions are well suited for standard field work, irrigation scheduling, and crop management.";
    }
}
