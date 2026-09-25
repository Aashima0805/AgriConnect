package com.agriconnect.service;

import com.agriconnect.dto.*;
import com.agriconnect.entity.*;
import com.agriconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final ProductService productService;
    private final OrderRepository orderRepo;
    private final OrderService orderService;
    private final GrievanceRepository grievanceRepo;
    private final GrievanceService grievanceService;
    private final DonationRepository donationRepo;
    private final DonationService donationService;
    private final EquipmentRepository equipmentRepo;
    private final EquipmentService equipmentService;
    private final ReviewService reviewService;

    public AdminService(UserRepository userRepo,
                        ProductRepository productRepo,
                        ProductService productService,
                        OrderRepository orderRepo,
                        OrderService orderService,
                        GrievanceRepository grievanceRepo,
                        GrievanceService grievanceService,
                        DonationRepository donationRepo,
                        DonationService donationService,
                        EquipmentRepository equipmentRepo,
                        EquipmentService equipmentService,
                        ReviewService reviewService) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.productService = productService;
        this.orderRepo = orderRepo;
        this.orderService = orderService;
        this.grievanceRepo = grievanceRepo;
        this.grievanceService = grievanceService;
        this.donationRepo = donationRepo;
        this.donationService = donationService;
        this.equipmentRepo = equipmentRepo;
        this.equipmentService = equipmentService;
        this.reviewService = reviewService;
    }

    /**
     * Verifies that the caller has ADMIN authorization.
     * Checks adminId, userId, header IDs, or Spring Security Principal.
     */
    public void verifyAdminAccess(Long adminId, Long userId, Long headerUserId, Long headerAdminId, Principal principal) {
        Long resolvedId = adminId != null ? adminId : (userId != null ? userId : (headerAdminId != null ? headerAdminId : headerUserId));

        if (resolvedId != null) {
            User user = userRepo.findById(resolvedId)
                    .orElseThrow(() -> new SecurityException("Unauthorized: User not found with ID " + resolvedId + ". Admin privileges required."));
            if (user.getRole() != User.Role.ADMIN) {
                throw new SecurityException("Unauthorized: Access denied. User role '" + user.getRole() + "' is not ADMIN.");
            }
            return;
        }

        if (principal != null) {
            String username = principal.getName();
            if (username != null) {
                User user = null;
                try {
                    user = userRepo.findById(Long.valueOf(username)).orElse(null);
                } catch (NumberFormatException ignored) {}
                if (user == null) {
                    user = userRepo.findByEmail(username.trim().toLowerCase()).orElse(null);
                }
                if (user == null || user.getRole() != User.Role.ADMIN) {
                    throw new SecurityException("Unauthorized: Admin privileges required.");
                }
            }
        }
    }

    // ==========================================
    // 1. DASHBOARD
    // ==========================================

    public AdminDashboardDto getDashboardStats() {
        AdminDashboardDto dto = new AdminDashboardDto();
        dto.setTotalUsers(userRepo.count());
        dto.setTotalFarmers(userRepo.countByRole(User.Role.FARMER));
        dto.setTotalCustomers(userRepo.countByRole(User.Role.CUSTOMER));
        dto.setTotalDonors(userRepo.countByRole(User.Role.DONOR));
        dto.setTotalProducts(productRepo.count());
        dto.setTotalOrders(orderRepo.count());

        List<Grievance> grievances = grievanceRepo.findAll();
        dto.setTotalGrievances(grievances.size());

        long openCount = 0;
        long partialCount = 0;
        long fundedCount = 0;
        long closedCount = 0;
        Map<String, Long> statusMap = new LinkedHashMap<>();

        for (Grievance g : grievances) {
            String st = g.getStatus() != null ? g.getStatus().toUpperCase() : "OPEN";
            statusMap.put(st, statusMap.getOrDefault(st, 0L) + 1);
            if ("OPEN".equals(st)) openCount++;
            else if ("PARTIALLY_FUNDED".equals(st)) partialCount++;
            else if ("FUNDED".equals(st)) fundedCount++;
            else if ("CLOSED".equals(st)) closedCount++;
        }

        dto.setOpenGrievances(openCount);
        dto.setPartiallyFundedGrievances(partialCount);
        dto.setFundedGrievances(fundedCount);
        dto.setClosedGrievances(closedCount);
        dto.setGrievancesByStatus(statusMap);

        dto.setTotalDonations(donationRepo.count());
        BigDecimal totalAmount = donationRepo.sumTotalDonations();
        dto.setTotalDonationAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
        dto.setTotalEquipment(equipmentRepo.count());

        return dto;
    }

    // ==========================================
    // 2. USER MANAGEMENT
    // ==========================================

    public List<UserResponse> listUsers(String role, String query) {
        List<User> list;
        if (role != null && !role.isBlank() && !role.equalsIgnoreCase("ALL")) {
            try {
                User.Role userRole = User.Role.valueOf(role.trim().toUpperCase());
                list = userRepo.findByRole(userRole);
            } catch (IllegalArgumentException e) {
                list = Collections.emptyList();
            }
        } else {
            list = userRepo.findAll();
        }

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            list = list.stream()
                    .filter(u -> (u.getName() != null && u.getName().toLowerCase().contains(q))
                            || (u.getEmail() != null && u.getEmail().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        return list.stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return userRepo.findById(id)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    public List<UserResponse> getUsersByRole(String role) {
        User.Role userRole = User.Role.valueOf(role.trim().toUpperCase());
        return userRepo.findByRole(userRole).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(Long id, Map<String, Object> updates) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (updates.containsKey("name") && updates.get("name") != null) {
            String name = updates.get("name").toString().trim();
            if (!name.isBlank()) user.setName(name);
        }
        if (updates.containsKey("phone")) {
            user.setPhone(updates.get("phone") != null ? updates.get("phone").toString().trim() : null);
        }
        if (updates.containsKey("address")) {
            user.setAddress(updates.get("address") != null ? updates.get("address").toString().trim() : null);
        }
        if (updates.containsKey("latitude")) {
            user.setLatitude(updates.get("latitude") != null ? Double.valueOf(updates.get("latitude").toString()) : null);
        }
        if (updates.containsKey("longitude")) {
            user.setLongitude(updates.get("longitude") != null ? Double.valueOf(updates.get("longitude").toString()) : null);
        }
        if (updates.containsKey("role") && updates.get("role") != null) {
            String roleStr = updates.get("role").toString().trim().toUpperCase();
            user.setRole(User.Role.valueOf(roleStr));
        }
        if (updates.containsKey("email") && updates.get("email") != null) {
            String newEmail = updates.get("email").toString().trim().toLowerCase();
            if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                if (userRepo.existsByEmail(newEmail)) {
                    throw new IllegalArgumentException("Email " + newEmail + " is already in use.");
                }
                user.setEmail(newEmail);
            }
        }

        User saved = userRepo.save(user);
        return UserResponse.fromEntity(saved);
    }

    // ==========================================
    // 3. PRODUCT MANAGEMENT
    // ==========================================

    public List<Product> listAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductEntityById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        return productService.updateProduct(id, null, dto);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productService.deleteProduct(id, null);
    }

    // ==========================================
    // 4. ORDER MANAGEMENT
    // ==========================================

    public List<Order> listAllOrders(String status) {
        List<Order> orders = orderRepo.findAll();
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            orders = orders.stream()
                    .filter(o -> o.getStatus().name().equalsIgnoreCase(status.trim()))
                    .collect(Collectors.toList());
        }
        return orders;
    }

    public OrderDto getOrderDetails(Long id) {
        return orderService.getOrderById(id, null);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, String status) {
        return orderService.updateOrderStatus(id, status);
    }

    // ==========================================
    // 5. GRIEVANCE MANAGEMENT
    // ==========================================

    public List<Grievance> listAllGrievances(String status) {
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            return grievanceRepo.findByStatusOrderByCreatedAtDesc(status.trim().toUpperCase());
        }
        return grievanceRepo.findAllByOrderByCreatedAtDesc();
    }

    public GrievanceDto getGrievanceById(Long id) {
        return grievanceService.getGrievanceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grievance not found with id: " + id));
    }

    @Transactional
    public GrievanceDto updateGrievanceStatus(Long id, String status) {
        return grievanceService.updateStatus(id, null, status);
    }

    // ==========================================
    // 6. DONATION MANAGEMENT
    // ==========================================

    public List<DonationDto> listAllDonations() {
        return donationService.getAllDonations();
    }

    public DonationDto getDonationById(Long id) {
        return donationService.getDonationById(id, null);
    }

    public List<DonationDto> getDonationsByGrievance(Long grievanceId) {
        return donationService.getGrievanceDonations(grievanceId);
    }

    // ==========================================
    // 7. EQUIPMENT MANAGEMENT
    // ==========================================

    public List<Equipment> listAllEquipment() {
        return equipmentRepo.findAll();
    }

    public EquipmentDto getEquipmentById(Long id) {
        return equipmentService.getEquipmentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment listing not found with id: " + id));
    }

    @Transactional
    public void deleteEquipment(Long id) {
        equipmentService.deleteEquipment(id, null);
    }

    // ==========================================
    // 8. REVIEW MODERATION
    // ==========================================

    public List<ReviewDto> listAllReviews() {
        return reviewService.getAllReviews();
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewService.deleteReview(id, null, true);
    }

    // ==========================================
    // 9. ADMIN REPORTS
    // ==========================================

    public AdminReportDto getReports() {
        AdminReportDto r = new AdminReportDto();

        // 1. Users by Role
        Map<String, Long> roleCounts = new LinkedHashMap<>();
        for (User.Role role : User.Role.values()) {
            roleCounts.put(role.name(), userRepo.countByRole(role));
        }
        r.setUsersByRole(roleCounts);

        // 2. Products by Category
        List<Product> products = productRepo.findAll();
        Map<String, Long> categories = new LinkedHashMap<>();
        for (Product p : products) {
            String cat = p.getCategory() != null ? p.getCategory() : "Other";
            categories.put(cat, categories.getOrDefault(cat, 0L) + 1);
        }
        r.setProductsByCategory(categories);
        r.setTotalProducts(products.size());

        // 3. Orders by Status
        List<Order> orders = orderRepo.findAll();
        Map<String, Long> orderStatusCounts = new LinkedHashMap<>();
        for (Order o : orders) {
            String s = o.getStatus().name();
            orderStatusCounts.put(s, orderStatusCounts.getOrDefault(s, 0L) + 1);
        }
        r.setOrdersByStatus(orderStatusCounts);
        r.setTotalOrders(orders.size());

        // 4. Grievances by Status
        List<Grievance> grievances = grievanceRepo.findAll();
        Map<String, Long> grievanceStatusCounts = new LinkedHashMap<>();
        for (Grievance g : grievances) {
            String s = g.getStatus() != null ? g.getStatus().toUpperCase() : "OPEN";
            grievanceStatusCounts.put(s, grievanceStatusCounts.getOrDefault(s, 0L) + 1);
        }
        r.setGrievancesByStatus(grievanceStatusCounts);
        r.setTotalGrievances(grievances.size());

        // 5. Donation totals
        BigDecimal totalDonations = donationRepo.sumTotalDonations();
        r.setTotalDonations(totalDonations != null ? totalDonations : BigDecimal.ZERO);
        r.setTotalDonationCount(donationRepo.count());

        // 6. Equipment count
        r.setTotalEquipment(equipmentRepo.count());

        return r;
    }
}
