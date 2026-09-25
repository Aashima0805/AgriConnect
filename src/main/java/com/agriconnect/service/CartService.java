package com.agriconnect.service;

import com.agriconnect.dto.CartItemDto;
import com.agriconnect.dto.CartResponseDto;
import com.agriconnect.entity.CartItem;
import com.agriconnect.entity.Product;
import com.agriconnect.entity.User;
import com.agriconnect.repository.CartItemRepository;
import com.agriconnect.repository.ProductRepository;
import com.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public CartService(CartItemRepository cartRepo, ProductRepository productRepo, UserRepository userRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public CartResponseDto getCart(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        User customer = userRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        List<CartItem> items = cartRepo.findByCustomerId(customerId);
        List<CartItemDto> itemDtos = items.stream()
                .map(CartItemDto::fromEntity)
                .collect(Collectors.toList());

        return new CartResponseDto(customerId, itemDtos);
    }

    @Transactional
    public CartItemDto addToCart(Long customerId, Long productId, Double quantity) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        User customer = userRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        if (!product.isAvailable() || product.getQuantity() <= 0) {
            throw new IllegalArgumentException("Product '" + product.getName() + "' is currently out of stock.");
        }

        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + quantity + " " + product.getUnit() +
                    ") exceeds available stock (" + product.getQuantity() + " " + product.getUnit() + ").");
        }

        CartItem item = cartRepo.findByCustomerIdAndProductId(customerId, productId)
                .map(existing -> {
                    double newQty = existing.getQuantity() + quantity;
                    if (newQty > product.getQuantity()) {
                        throw new IllegalArgumentException("Cannot add " + quantity + " more. Total in cart (" + newQty +
                                " " + product.getUnit() + ") would exceed available stock (" + product.getQuantity() + " " + product.getUnit() + ").");
                    }
                    existing.setQuantity(newQty);
                    return existing;
                })
                .orElseGet(() -> new CartItem(customer, product, quantity));

        CartItem saved = cartRepo.save(item);
        return CartItemDto.fromEntity(saved);
    }

    @Transactional
    public CartItemDto updateCartQuantity(Long customerId, Long itemId, Double quantity) {
        if (itemId == null) {
            throw new IllegalArgumentException("Cart item ID is required.");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity is required.");
        }

        CartItem item = cartRepo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with ID: " + itemId));

        if (customerId != null && !item.getCustomer().getId().equals(customerId)) {
            throw new SecurityException("Unauthorized: You can only update items in your own cart.");
        }

        if (quantity <= 0) {
            cartRepo.delete(item);
            return null;
        }

        Product product = item.getProduct();
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + quantity + " " + product.getUnit() +
                    ") exceeds available stock (" + product.getQuantity() + " " + product.getUnit() + ").");
        }

        item.setQuantity(quantity);
        CartItem saved = cartRepo.save(item);
        return CartItemDto.fromEntity(saved);
    }

    @Transactional
    public void removeCartItem(Long customerId, Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Cart item ID is required.");
        }
        CartItem item = cartRepo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with ID: " + itemId));

        if (customerId != null && !item.getCustomer().getId().equals(customerId)) {
            throw new SecurityException("Unauthorized: You can only remove items from your own cart.");
        }

        cartRepo.delete(item);
    }

    @Transactional
    public void clearCart(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        cartRepo.deleteByCustomerId(customerId);
    }
}
