package com.agriconnect.config;

import com.agriconnect.entity.*;
import com.agriconnect.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDatabase(UserRepository users,
                                   ProductRepository products,
                                   EquipmentRepository equipment,
                                   GrievanceRepository grievances,
                                   DonationRepository donations,
                                   FarmDetailsRepository farms,
                                   RatingRepository ratings,
                                   MarketPriceRepository marketPrices,
                                   PasswordEncoder encoder) {
        return args -> {
            // 1. Seed standard demo accounts
            User farmer = users.findByEmail("farmer@agriconnect.com").orElseGet(() -> {
                User u = new User("Ramesh Kumar (Farmer)", "farmer@agriconnect.com", encoder.encode("farmer123"), User.Role.FARMER);
                u.setPhone("9876543210");
                u.setAddress("Green Valley Farm, Pollachi Road, Coimbatore, Tamil Nadu");
                u.setLatitude(11.0168);
                u.setLongitude(76.9558);
                return users.save(u);
            });

            User customer = users.findByEmail("customer@agriconnect.com").orElseGet(() -> {
                User u = new User("Priya Sharma (Customer)", "customer@agriconnect.com", encoder.encode("customer123"), User.Role.CUSTOMER);
                u.setPhone("9845123456");
                u.setAddress("42 Lakeview Colony, RS Puram, Coimbatore");
                return users.save(u);
            });

            User donor = users.findByEmail("donor@agriconnect.com").orElseGet(() -> {
                User u = new User("Anand Patel (Donor)", "donor@agriconnect.com", encoder.encode("donor123"), User.Role.DONOR);
                u.setPhone("9823098765");
                u.setAddress("Koramangala, Bangalore, Karnataka");
                return users.save(u);
            });

            users.findByEmail("admin@agriconnect.com").orElseGet(() -> {
                User u = new User("System Administrator", "admin@agriconnect.com", encoder.encode("admin123"), User.Role.ADMIN);
                u.setPhone("9900112233");
                u.setAddress("AgriConnect Administrative HQ");
                return users.save(u);
            });

            // 2. Seed Farm Details
            if (farms.findByFarmerId(farmer.getId()).isEmpty()) {
                FarmDetails f = new FarmDetails();
                f.setFarmer(farmer);
                f.setLandArea(4.5);
                f.setLandUnit("acres");
                f.setLocation("Pollachi, Coimbatore, Tamil Nadu");
                f.setCrops("Rice (Paddy), Tomato, Maize");
                f.setSoilType("Alluvial Loam");
                f.setIrrigationType("Drip & Canal Irrigation");
                farms.save(f);
            }

            // 3. Seed Products
            if (products.count() == 0) {
                Product p1 = new Product();
                p1.setFarmer(farmer);
                p1.setName("Fresh Organic Basmati Paddy");
                p1.setCategory("Grains");
                p1.setDescription("Naturally cultivated aromatic long-grain paddy, harvested and sun-dried.");
                p1.setPrice(new BigDecimal("48.00"));
                p1.setQuantity(500.0);
                p1.setUnit("kg");
                p1.setAvailable(true);
                p1.setImageUrl("https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop");
                products.save(p1);

                Product p2 = new Product();
                p2.setFarmer(farmer);
                p2.setName("Farm Fresh Vine Tomatoes");
                p2.setCategory("Vegetables");
                p2.setDescription("Pesticide-free red ripe tomatoes direct from greenhouse drip-irrigated beds.");
                p2.setPrice(new BigDecimal("35.00"));
                p2.setQuantity(200.0);
                p2.setUnit("kg");
                p2.setAvailable(true);
                p2.setImageUrl("https://images.unsplash.com/photo-1546470427-227c7369a924?w=500&auto=format&fit=crop");
                products.save(p2);

                Product p3 = new Product();
                p3.setFarmer(farmer);
                p3.setName("Golden Sweet Corn");
                p3.setCategory("Grains");
                p3.setDescription("Freshly picked tender sweet corn cobs, high nutrition and naturally sweet.");
                p3.setPrice(new BigDecimal("30.00"));
                p3.setQuantity(150.0);
                p3.setUnit("kg");
                p3.setAvailable(true);
                p3.setImageUrl("https://images.unsplash.com/photo-1551754655-cd27e38d2076?w=500&auto=format&fit=crop");
                products.save(p3);

                Product p4 = new Product();
                p4.setFarmer(farmer);
                p4.setName("Cold Pressed Groundnut Pods");
                p4.setCategory("Oil Seeds");
                p4.setDescription("Sun-cured rich groundnut kernels suitable for cold press extraction or roasting.");
                p4.setPrice(new BigDecimal("85.00"));
                p4.setQuantity(120.0);
                p4.setUnit("kg");
                p4.setAvailable(true);
                p4.setImageUrl("https://images.unsplash.com/photo-1598449356475-b9f71db7d847?w=500&auto=format&fit=crop");
                products.save(p4);
            }

            // 4. Seed Equipment Listings
            if (equipment.count() == 0) {
                Equipment e1 = new Equipment();
                e1.setOwner(farmer);
                e1.setName("Mini 25HP 4WD Field Tractor");
                e1.setType("Mini Tractor");
                e1.setDescription("Compact 25HP multi-purpose tractor with rotavator attachment, ideal for small paddy fields.");
                e1.setRentalPricePerDay(new BigDecimal("1800.00"));
                e1.setLocation("Coimbatore Rural");
                e1.setAvailable(true);
                equipment.save(e1);

                Equipment e2 = new Equipment();
                e2.setOwner(farmer);
                e2.setName("High-Pressure Orchard Sprayer");
                e2.setType("Sprayer");
                e2.setDescription("Battery-assisted 16-liter knapsack sprayer with adjustable dual nozzles.");
                e2.setRentalPricePerDay(new BigDecimal("350.00"));
                e2.setLocation("Coimbatore Rural");
                e2.setAvailable(true);
                equipment.save(e2);

                Equipment e3 = new Equipment();
                e3.setOwner(farmer);
                e3.setName("Rotary Power Tiller 8HP");
                e3.setType("Power Tiller");
                e3.setDescription("Heavy-duty diesel power tiller with deep soil tilling blades for inter-cultivation.");
                e3.setRentalPricePerDay(new BigDecimal("1100.00"));
                e3.setLocation("Coimbatore Rural");
                e3.setAvailable(true);
                equipment.save(e3);
            }

            // 5. Seed Grievance
            if (grievances.count() == 0) {
                Grievance g1 = new Grievance();
                g1.setFarmer(farmer);
                g1.setTitle("Sudden Cloudburst & Paddy Field Inundation");
                g1.setDescription("Unseasonal torrential rain caused severe flooding across 2 acres of flowering paddy fields. Field bunds were broken and seedlings were partially submerged. Financial assistance will help in pumping out water and re-seeding damaged patches.");
                g1.setCategory("Flood / Rain Damage");
                g1.setTargetAmount(new BigDecimal("30000.00"));
                g1.setReceivedAmount(new BigDecimal("8000.00"));
                g1.setStatus("PARTIALLY_FUNDED");
                g1.setLocation("Pollachi, Coimbatore");
                Grievance savedGrievance = grievances.save(g1);

                if (donations.count() == 0) {
                    Donation d1 = new Donation(
                            donor,
                            savedGrievance,
                            new BigDecimal("8000.00"),
                            "SUCCESS",
                            "REC-INIT-1001",
                            "TXN-MOCK-SEED-1001"
                    );
                    donations.save(d1);
                }
            }

            // 6. Seed Sample Initial Rating
            if (ratings.count() == 0) {
                Rating r = new Rating();
                r.setCustomer(customer);
                r.setFarmer(farmer);
                r.setOrderId(1L);
                r.setProductQuality(5);
                r.setDeliveryExperience(5);
                r.setComment("Exceptional quality basmati paddy! Very fresh and cleanly packaged. Highly recommend this farmer.");
                ratings.save(r);
            }

            // 7. Seed Sample Reference Market Prices (Clearly marked with isDemo = true)
            // 7. Seed Official MSP for Marketing Season 2026-27
if (marketPrices.count() == 0) {

    // Kharif Crops - Marketing Season 2026-27
    LocalDate kharifDate = LocalDate.of(2026, 5, 13);

    marketPrices.save(new MarketPrice("Paddy (Common)", "All India", new BigDecimal("2441"), "quintal", kharifDate, false, "Cereals", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Paddy (Grade A)", "All India", new BigDecimal("2461"), "quintal", kharifDate, false, "Cereals", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Jowar (Hybrid)", "All India", new BigDecimal("4023"), "quintal", kharifDate, false, "Cereals", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Jowar (Maldandi)", "All India", new BigDecimal("4073"), "quintal", kharifDate, false, "Cereals", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Bajra", "All India", new BigDecimal("2900"), "quintal", kharifDate, false, "Cereals", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Ragi", "All India", new BigDecimal("5205"), "quintal", kharifDate, false, "Cereals", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Maize", "All India", new BigDecimal("2410"), "quintal", kharifDate, false, "Cereals", "India", "Official MSP"));

    marketPrices.save(new MarketPrice("Tur (Arhar)", "All India", new BigDecimal("8450"), "quintal", kharifDate, false, "Pulses", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Moong", "All India", new BigDecimal("8780"), "quintal", kharifDate, false, "Pulses", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Urad", "All India", new BigDecimal("8200"), "quintal", kharifDate, false, "Pulses", "India", "Official MSP"));

    marketPrices.save(new MarketPrice("Groundnut-in-shell", "All India", new BigDecimal("7517"), "quintal", kharifDate, false, "Oilseeds", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Soyabean (Yellow)", "All India", new BigDecimal("5708"), "quintal", kharifDate, false, "Oilseeds", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Sunflower Seed", "All India", new BigDecimal("8343"), "quintal", kharifDate, false, "Oilseeds", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Sesamum", "All India", new BigDecimal("10346"), "quintal", kharifDate, false, "Oilseeds", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Nigerseed", "All India", new BigDecimal("10052"), "quintal", kharifDate, false, "Oilseeds", "India", "Official MSP"));

    marketPrices.save(new MarketPrice("Cotton (Medium Staple)", "All India", new BigDecimal("8267"), "quintal", kharifDate, false, "Commercial Crops", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Cotton (Long Staple)", "All India", new BigDecimal("8667"), "quintal", kharifDate, false, "Commercial Crops", "India", "Official MSP"));

    // Rabi Crops - Marketing Season 2026-27
    LocalDate rabiDate = LocalDate.of(2025, 12, 2);

    marketPrices.save(new MarketPrice("Wheat", "All India", new BigDecimal("2585"), "quintal", rabiDate, false, "Cereals", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Barley", "All India", new BigDecimal("2150"), "quintal", rabiDate, false, "Cereals", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Gram", "All India", new BigDecimal("5875"), "quintal", rabiDate, false, "Pulses", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Masur (Lentil)", "All India", new BigDecimal("7000"), "quintal", rabiDate, false, "Pulses", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Rapeseed & Mustard", "All India", new BigDecimal("6200"), "quintal", rabiDate, false, "Oilseeds", "India", "Official MSP"));
    marketPrices.save(new MarketPrice("Safflower", "All India", new BigDecimal("6540"), "quintal", rabiDate, false, "Oilseeds", "India", "Official MSP"));

    LocalDate copraDate = LocalDate.of(2025, 12, 12);
    marketPrices.save(new MarketPrice("Jute", "All India", new BigDecimal("5925"), "quintal", rabiDate, false, "Commercial Crops", "India", "Official MSP"));
marketPrices.save(new MarketPrice("Copra (Milling)", "All India", new BigDecimal("12027"), "quintal", rabiDate, false, "Commercial Crops", "India", "Official MSP"));
marketPrices.save(new MarketPrice("Copra (Ball)", "All India", new BigDecimal("12500"), "quintal", rabiDate, false, "Commercial Crops", "India", "Official MSP"));
}
        };
    }
}
