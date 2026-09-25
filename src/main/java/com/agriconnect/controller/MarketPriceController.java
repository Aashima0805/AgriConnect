package com.agriconnect.controller;

import com.agriconnect.dto.MarketPriceDto;
import com.agriconnect.service.MarketPriceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market-prices")
@CrossOrigin(origins = "*")
public class MarketPriceController {

    private final MarketPriceService marketPriceService;

    public MarketPriceController(MarketPriceService marketPriceService) {
        this.marketPriceService = marketPriceService;
    }

    @GetMapping
    public List<MarketPriceDto> getMarketPrices(@RequestParam(required = false) String crop,
                                                @RequestParam(required = false) String query) {
        String filter = (crop != null && !crop.isBlank()) ? crop : query;
        return marketPriceService.getMarketPrices(filter);
    }

    @GetMapping("/crop/{cropName}")
    public List<MarketPriceDto> getPricesByCrop(@PathVariable String cropName) {
        return marketPriceService.getPricesByCrop(cropName);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketPriceDto> getPriceById(@PathVariable Long id) {
        return marketPriceService.getPriceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addMarketPrice(@Valid @RequestBody MarketPriceDto dto) {
        try {
            MarketPriceDto created = marketPriceService.addMarketPrice(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
