package com.agriconnect.controller;

import com.agriconnect.dto.WeatherDto;
import com.agriconnect.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherDto> getWeather(@RequestParam(required = false) Double lat,
                                                 @RequestParam(required = false) Double lon,
                                                 @RequestParam(required = false) String location) {
        WeatherDto weather = weatherService.getWeather(lat, lon, location);
        if ("error".equals(weather.getStatus())) {
            return ResponseEntity.badRequest().body(weather);
        }
        return ResponseEntity.ok(weather);
    }
}
