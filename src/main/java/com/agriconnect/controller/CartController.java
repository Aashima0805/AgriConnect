package com.agriconnect.controller;

import com.agriconnect.dto.CartItemDto;
import com.agriconnect.dto.CartResponseDto;
import com.agriconnect.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping({"/{customerId}", "/customer/{customerId}"})
    public ResponseEntity<?> getCart(@PathVariable Long customerId) {
        try {
            CartResponseDto response = cartService.getCart(customerId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartItemDto req) {
        try {
            CartItemDto item = cartService.addToCart(req.getCustomerId(), req.getProductId(), req.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<?> addToCartByPath(@PathVariable Long customerId, @RequestBody Map<String, Object> req) {
        try {
            Long productId = Long.valueOf(req.get("productId").toString());
            double quantity = Double.parseDouble(req.get("quantity").toString());
            CartItemDto item = cartService.addToCart(customerId, productId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long customerId,
                                            @PathVariable Long itemId,
                                            @RequestBody Map<String, Object> req) {
        try {
            double quantity = Double.parseDouble(req.get("quantity").toString());
            CartItemDto updated = cartService.updateCartQuantity(customerId, itemId, quantity);
            if (updated == null) {
                return ResponseEntity.ok(Map.of("status", "success", "message", "Item removed from cart"));
            }
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateQuantityDirect(@PathVariable Long itemId,
                                                  @RequestParam(required = false) Long customerId,
                                                  @RequestBody Map<String, Object> req) {
        try {
            double quantity = Double.parseDouble(req.get("quantity").toString());
            CartItemDto updated = cartService.updateCartQuantity(customerId, itemId, quantity);
            if (updated == null) {
                return ResponseEntity.ok(Map.of("status", "success", "message", "Item removed from cart"));
            }
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping({"/{customerId}/items/{itemId}", "/items/{itemId}"})
    public ResponseEntity<?> removeFromCart(@PathVariable(required = false) Long customerId,
                                            @PathVariable Long itemId) {
        try {
            cartService.removeCartItem(customerId, itemId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Item removed from cart"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping({"/{customerId}", "/{customerId}/clear", "/customer/{customerId}/clear"})
    public ResponseEntity<?> clearCart(@PathVariable Long customerId) {
        try {
            cartService.clearCart(customerId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Cart cleared successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
