/**
 * AgriConnect Common Authentication & Navigation Guard (Phase 1)
 */

const AgriAuth = {
    STORAGE_KEY: 'agri_user',

    /**
     * Get stored user profile from localStorage
     */
    getUser() {
        try {
            const raw = localStorage.getItem(this.STORAGE_KEY);
            return raw ? JSON.parse(raw) : null;
        } catch (e) {
            console.error('Failed to parse user session', e);
            return null;
        }
    },

    /**
     * Store minimum required user profile
     */
    setUser(user) {
        if (!user) return;
        const safeUser = {
            id: user.id,
            name: user.name,
            email: user.email,
            role: user.role,
            phone: user.phone || '',
            address: user.address || '',
            latitude: user.latitude || null,
            longitude: user.longitude || null
        };
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(safeUser));
    },

    /**
     * Clear stored user session
     */
    clearUser() {
        localStorage.removeItem(this.STORAGE_KEY);
    },

    /**
     * Resolve dashboard URL by role
     */
    getDashboardUrl(role) {
        switch (role) {
            case 'FARMER':
                return 'farmer-dashboard.html';
            case 'CUSTOMER':
                return 'customer-dashboard.html';
            case 'DONOR':
                return 'donor-dashboard.html';
            case 'ADMIN':
                return 'admin-dashboard.html';
            default:
                return 'login.html';
        }
    },

    /**
     * Authentication & Role Guard for protected pages
     */
    requireAuth(expectedRole = null) {
        const user = this.getUser();
        if (!user) {
            window.location.replace('login.html');
            return null;
        }

        if (expectedRole && user.role !== expectedRole) {
            const correctUrl = this.getDashboardUrl(user.role);
            window.location.replace(correctUrl);
            return null;
        }

        return user;
    },

    /**
     * Execute logout: API call, session clear, redirect
     */
    async logout() {
        try {
            await fetch('/api/auth/logout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
        } catch (e) {
            console.warn('Backend logout call completed with warning', e);
        } finally {
            this.clearUser();
            window.location.href = 'login.html';
        }
    },

    /**
     * Render the standardized AgriConnect navbar
     */
    renderNavbar(containerId = 'navbar-container', activePage = 'dashboard') {
        const container = document.getElementById(containerId);
        if (!container) return;

        const user = this.getUser();
        const roleClass = user ? `role-${user.role.toLowerCase()}` : '';
        const roleLabel = user ? user.role.charAt(0) + user.role.slice(1).toLowerCase() : '';
        const dashboardUrl = user ? this.getDashboardUrl(user.role) : 'login.html';

        let navLinksHtml = '';
        if (user && user.role === 'CUSTOMER') {
            navLinksHtml = `
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'dashboard' ? 'active' : ''}" href="customer-dashboard.html"><i class="bi bi-grid"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'marketplace' ? 'active' : ''}" href="customer-marketplace.html"><i class="bi bi-shop"></i> Marketplace</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'nearby' ? 'active' : ''}" href="customer-nearby.html"><i class="bi bi-geo-alt"></i> Nearby Farmers</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'cart' ? 'active' : ''}" href="customer-cart.html"><i class="bi bi-cart3"></i> Cart</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'orders' ? 'active' : ''}" href="customer-orders.html"><i class="bi bi-receipt-cutoff"></i> Orders</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'reviews' ? 'active' : ''}" href="customer-reviews.html"><i class="bi bi-star"></i> Reviews</a></li>
            `;
        } else if (user && user.role === 'DONOR') {
            navLinksHtml = `
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'dashboard' ? 'active' : ''}" href="donor-dashboard.html"><i class="bi bi-grid"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'grievances' ? 'active' : ''}" href="#support-section"><i class="bi bi-heart-pulse"></i> Farmer Support</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'donations' ? 'active' : ''}" href="#history-section"><i class="bi bi-clock-history"></i> Donation History</a></li>
            `;
        } else if (user && user.role === 'ADMIN') {
            navLinksHtml = `
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'dashboard' ? 'active' : ''}" href="admin-dashboard.html"><i class="bi bi-speedometer2"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link" href="#users-section"><i class="bi bi-people"></i> Users</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link" href="#products-section"><i class="bi bi-basket"></i> Products</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link" href="#orders-section"><i class="bi bi-receipt"></i> Orders</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link" href="#grievances-section"><i class="bi bi-chat-heart"></i> Grievances</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link" href="#donations-section"><i class="bi bi-cash-stack"></i> Donations</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link" href="#equipment-section"><i class="bi bi-truck"></i> Equipment</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link" href="#reviews-section"><i class="bi bi-star"></i> Reviews</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link" href="#reports-section"><i class="bi bi-bar-chart"></i> Reports</a></li>
            `;
        } else {
            navLinksHtml = `
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'dashboard' ? 'active' : ''}" href="${dashboardUrl}"><i class="bi bi-grid"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'farm' ? 'active' : ''}" href="farmer-farm.html"><i class="bi bi-geo-alt"></i> My Farm</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'products' ? 'active' : ''}" href="farmer-products.html"><i class="bi bi-basket3"></i> My Products</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'equipment' ? 'active' : ''}" href="farmer-equipment.html"><i class="bi bi-truck"></i> Equipment</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'disease' || activePage === 'crop-disease' ? 'active' : ''}" href="crop-disease.html"><i class="bi bi-virus"></i> Crop Doctor</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'fertilizer' ? 'active' : ''}" href="fertilizer.html"><i class="bi bi-droplet-half"></i> Fertilizer</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'weather' ? 'active' : ''}" href="weather.html"><i class="bi bi-cloud-sun"></i> Weather</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'market-prices' ? 'active' : ''}" href="market-prices.html"><i class="bi bi-graph-up"></i> Market Prices</a></li>
                <li class="nav-item"><a class="nav-link agri-nav-link ${activePage === 'grievances' ? 'active' : ''}" href="farmer-grievances.html"><i class="bi bi-chat-heart"></i> Grievances</a></li>
            `;
        }

        container.innerHTML = `
            <nav class="navbar navbar-expand-lg agri-navbar sticky-top">
                <div class="container">
                    <a class="navbar-brand agri-brand" href="${dashboardUrl}">
                        <i class="bi bi-flower1"></i> AgriConnect
                    </a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navContent" aria-controls="navContent" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navContent">
                        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                            ${navLinksHtml}
                        </ul>
                        ${user ? `
                        <div class="d-flex align-items-center gap-3 flex-wrap">
                            <div class="agri-user-badge">
                                <i class="bi bi-person-circle text-success"></i>
                                <span>${this.escapeHtml(user.name)}</span>
                                <span class="agri-role-pill ${roleClass}">${roleLabel}</span>
                            </div>
                            <button type="button" class="btn btn-outline-danger btn-sm" onclick="AgriAuth.logout()">
                                <i class="bi bi-box-arrow-right"></i> Logout
                            </button>
                        </div>
                        ` : `
                        <div class="d-flex align-items-center gap-2">
                            <a href="login.html" class="btn btn-agri-secondary btn-sm">Sign In</a>
                            <a href="register.html" class="btn btn-agri-primary btn-sm">Register</a>
                        </div>
                        `}
                    </div>
                </div>
            </nav>
        `;
    },

    /**
     * Show a standardized "Coming Soon / Phase 2" modal
     */
    showComingSoon(featureName, description = null) {
        let modalEl = document.getElementById('agriComingSoonModal');
        if (!modalEl) {
            modalEl = document.createElement('div');
            modalEl.id = 'agriComingSoonModal';
            modalEl.className = 'modal fade';
            modalEl.tabIndex = -1;
            modalEl.innerHTML = `
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content" style="border-radius: 16px; border: 1px solid var(--agri-border); overflow: hidden;">
                        <div class="modal-body p-4 text-center">
                            <div class="mx-auto mb-3 d-inline-flex align-items-center justify-content-center" style="width: 56px; height: 56px; border-radius: 50%; background: #E8F5E9; color: #1B5E20; font-size: 1.75rem;">
                                <i class="bi bi-clock-history"></i>
                            </div>
                            <h5 class="fw-bold text-dark mb-2" id="csModalTitle">Module Scheduled</h5>
                            <p class="text-muted small mb-3" id="csModalDesc">This feature is part of upcoming Phase 2 module implementations.</p>
                            <span class="badge bg-success-subtle text-success px-3 py-1 rounded-pill mb-4">Phase 2 Placeholder</span>
                            <div>
                                <button type="button" class="btn btn-agri-primary px-4" data-bs-dismiss="modal">Got it</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            document.body.appendChild(modalEl);
        }

        document.getElementById('csModalTitle').textContent = featureName;
        if (description) {
            document.getElementById('csModalDesc').textContent = description;
        } else {
            document.getElementById('csModalDesc').textContent = `The ${featureName} interface and workflow will be implemented in the next phase.`;
        }

        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    },

    escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }
};

window.AgriAuth = AgriAuth;
