package com.agriconnect.service;

import com.agriconnect.entity.User;
import com.agriconnect.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String name, String email, String rawPassword, String phone, String address, User.Role role, Double lat, Double lon) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (userRepository.existsByEmail(email.trim().toLowerCase())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhone(phone != null ? phone.trim() : null);
        user.setAddress(address != null ? address.trim() : null);
        user.setRole(role != null ? role : User.Role.CUSTOMER);
        user.setLatitude(lat);
        user.setLongitude(lon);

        return userRepository.save(user);
    }

    public Optional<User> authenticate(String email, String rawPassword) {
        if (email == null || rawPassword == null) return Optional.empty();
        Optional<User> opt = userRepository.findByEmail(email.trim().toLowerCase());
        if (opt.isPresent() && passwordEncoder.matches(rawPassword, opt.get().getPassword())) {
            return opt;
        }
        return Optional.empty();
    }

    public User updateProfile(Long userId, String name, String phone, String address, Double lat, Double lon) {
        User u = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (name != null && !name.isBlank()) u.setName(name.trim());
        if (phone != null) u.setPhone(phone.trim());
        if (address != null) u.setAddress(address.trim());
        if (lat != null) u.setLatitude(lat);
        if (lon != null) u.setLongitude(lon);
        return userRepository.save(u);
    }
}
