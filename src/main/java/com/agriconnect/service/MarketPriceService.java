package com.agriconnect.service;

import com.agriconnect.dto.MarketPriceDto;
import com.agriconnect.entity.MarketPrice;
import com.agriconnect.repository.MarketPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarketPriceService {

    private final MarketPriceRepository marketPriceRepo;

    public MarketPriceService(MarketPriceRepository marketPriceRepo) {
        this.marketPriceRepo = marketPriceRepo;
    }

    public List<MarketPriceDto> getMarketPrices(String query) {
        List<MarketPrice> list;
        if (query != null && !query.isBlank()) {
            String q = query.trim();
            list = marketPriceRepo.findByCropNameContainingIgnoreCaseOrMarketContainingIgnoreCaseOrCategoryContainingIgnoreCase(q, q, q);
        } else {
            list = marketPriceRepo.findAllByOrderByDateDesc();
        }
        return list.stream().map(MarketPriceDto::fromEntity).collect(Collectors.toList());
    }

    public List<MarketPriceDto> getPricesByCrop(String cropName) {
        if (cropName == null || cropName.isBlank()) {
            throw new IllegalArgumentException("Crop name is required.");
        }
        return marketPriceRepo.findByCropNameContainingIgnoreCase(cropName.trim())
                .stream()
                .map(MarketPriceDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<MarketPriceDto> getPriceById(Long id) {
        return marketPriceRepo.findById(id).map(MarketPriceDto::fromEntity);
    }

    @Transactional
    public MarketPriceDto addMarketPrice(MarketPriceDto dto) {
        if (dto.getCropName() == null || dto.getCropName().isBlank()) {
            throw new IllegalArgumentException("Crop name is required.");
        }
        if (dto.getMarket() == null || dto.getMarket().isBlank()) {
            throw new IllegalArgumentException("Market is required.");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }

        MarketPrice mp = new MarketPrice();
        mp.setCropName(dto.getCropName().trim());
        mp.setMarket(dto.getMarket().trim());
        mp.setPrice(dto.getPrice());
        mp.setUnit(dto.getUnit() != null && !dto.getUnit().isBlank() ? dto.getUnit().trim() : "quintal");
        mp.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());
        mp.setIsDemo(dto.getIsDemo() != null ? dto.getIsDemo() : false);
        mp.setCategory(dto.getCategory() != null ? dto.getCategory().trim() : "Grains");
        mp.setState(dto.getState());
        mp.setPriceType(dto.getPriceType() != null ? dto.getPriceType() : "Mandi Market Rate");

        MarketPrice saved = marketPriceRepo.save(mp);
        return MarketPriceDto.fromEntity(saved);
    }
}
