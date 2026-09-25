package com.agriconnect.controller;

import com.agriconnect.entity.Notification;
import com.agriconnect.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationRepository notificationRepo;

    public NotificationController(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return notificationRepo.findById(id).map(n -> {
            n.setReadFlag(true);
            return ResponseEntity.ok(notificationRepo.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/unread-count")
    public Map<String, Long> getUnreadCount(@PathVariable Long userId) {
        return Map.of("count", notificationRepo.countByUserIdAndReadFlagFalse(userId));
    }
}
