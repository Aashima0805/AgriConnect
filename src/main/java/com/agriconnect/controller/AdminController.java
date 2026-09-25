package com.agriconnect.controller;

import com.agriconnect.dto.*;
import com.agriconnect.entity.*;
import com.agriconnect.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ==========================================
    // 1. ADMIN DASHBOARD
    // ==========================================

    @GetMapping({"/dashboard", "/stats"})
    public ResponseEntity<?> getDashboardStats(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            AdminDashboardDto stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 2. USER MANAGEMENT
    // ==========================================

    @GetMapping("/users")
    public ResponseEntity<?> listUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<UserResponse> users = adminService.listUsers(role, query);
            return ResponseEntity.ok(users);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            UserResponse user = adminService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<?> getUsersByRole(
            @PathVariable String role,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<UserResponse> users = adminService.getUsersByRole(role);
            return ResponseEntity.ok(users);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + role));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            UserResponse updated = adminService.updateUser(id, updates);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 3. PRODUCT MANAGEMENT
    // ==========================================

    @GetMapping("/products")
    public ResponseEntity<?> listAllProducts(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<Product> products = adminService.listAllProducts();
            return ResponseEntity.ok(products);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            Product product = adminService.getProductEntityById(id);
            return ResponseEntity.ok(product);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto dto,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            ProductDto updated = adminService.updateProduct(id, dto);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            adminService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Product deleted successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 4. ORDER MANAGEMENT
    // ==========================================

    @GetMapping("/orders")
    public ResponseEntity<?> listAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<Order> orders = adminService.listAllOrders(status);
            return ResponseEntity.ok(orders);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderDetails(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            OrderDto order = adminService.getOrderDetails(id);
            return ResponseEntity.ok(order);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(
            @PathVariable String status,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<Order> orders = adminService.listAllOrders(status);
            return ResponseEntity.ok(orders);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    
    // ==========================================
    // 5. GRIEVANCE MANAGEMENT
    // ==========================================

    @GetMapping("/grievances")
    public ResponseEntity<?> listAllGrievances(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<Grievance> list = adminService.listAllGrievances(status);
            return ResponseEntity.ok(list);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/grievances/{id}")
    public ResponseEntity<?> getGrievanceById(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            GrievanceDto dto = adminService.getGrievanceById(id);
            return ResponseEntity.ok(dto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/grievances/{id}/status")
    public ResponseEntity<?> updateGrievanceStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> req,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            String status = req.get("status");
            if (status == null || status.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required."));
            }
            GrievanceDto updated = adminService.updateGrievanceStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 6. DONATION MANAGEMENT
    // ==========================================

    @GetMapping("/donations")
    public ResponseEntity<?> listAllDonations(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<DonationDto> donations = adminService.listAllDonations();
            return ResponseEntity.ok(donations);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/donations/{id}")
    public ResponseEntity<?> getDonationById(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            DonationDto donation = adminService.getDonationById(id);
            return ResponseEntity.ok(donation);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/donations/grievance/{id}")
    public ResponseEntity<?> getDonationsByGrievance(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<DonationDto> donations = adminService.getDonationsByGrievance(id);
            return ResponseEntity.ok(donations);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 7. EQUIPMENT MANAGEMENT
    // ==========================================

    @GetMapping("/equipment")
    public ResponseEntity<?> listAllEquipment(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<Equipment> equipment = adminService.listAllEquipment();
            return ResponseEntity.ok(equipment);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/equipment/{id}")
    public ResponseEntity<?> getEquipmentById(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            EquipmentDto equipment = adminService.getEquipmentById(id);
            return ResponseEntity.ok(equipment);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/equipment/{id}")
    public ResponseEntity<?> deleteEquipment(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            adminService.deleteEquipment(id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Equipment listing removed successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 8. ADMIN REPORTS
    // ==========================================

    @GetMapping("/reports")
    public ResponseEntity<?> getReports(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            AdminReportDto reports = adminService.getReports();
            return ResponseEntity.ok(reports);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 9. REVIEW MODERATION
    // ==========================================

    @GetMapping("/reviews")
    public ResponseEntity<?> listAllReviews(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            List<ReviewDto> reviews = adminService.listAllReviews();
            return ResponseEntity.ok(reviews);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long id,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long headerAdminId,
            Principal principal) {
        try {
            adminService.verifyAdminAccess(adminId, userId, headerUserId, headerAdminId, principal);
            adminService.deleteReview(id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Review removed by administrator"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
