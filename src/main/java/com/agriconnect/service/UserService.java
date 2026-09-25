package com.agriconnect.service;

import com.agriconnect.dto.LoginRequest;
import com.agriconnect.dto.RegisterRequest;
import com.agriconnect.dto.UserResponse;
import com.agriconnect.entity.User;
import com.agriconnect.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        user.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.CUSTOMER);
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadCredentialsException("Email and password are required.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        return UserResponse.fromEntity(user);
    }

    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id).map(UserResponse::fromEntity);
    }

    public Optional<User> findEntityById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, String name, String phone, String address, Double lat, Double lon) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (name != null && !name.isBlank()) user.setName(name.trim());
        if (phone != null) user.setPhone(phone.trim());
        if (address != null) user.setAddress(address.trim());
        if (lat != null) user.setLatitude(lat);
        if (lon != null) user.setLongitude(lon);

        User updatedUser = userRepository.save(user);
        return UserResponse.fromEntity(updatedUser);
    }
}
