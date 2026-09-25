// AgriConnect Main Frontend Application Logic
let currentUser = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    checkSession();
});

function checkSession() {
    const raw = localStorage.getItem('agri_user');
    if (!raw) {
        window.location.href = 'login.html';
        return;
    }
    currentUser = JSON.parse(raw);

if (document.getElementById('workspaceContent')) {
    setupNavigation();
    loadRoleDefaultView();
}
}

function logout() {
    localStorage.removeItem('agri_user');
    fetch('/api/auth/logout', { method: 'POST' }).catch(() => {});
    window.location.href = 'index.html';
}

function showToast(message, isError = false) {
    const toastEl = document.getElementById('agriToast');
    const toastMsg = document.getElementById('toastMessage');
    toastEl.className = `toast align-items-center text-white border-0 ${isError ? 'bg-danger' : 'bg-success'}`;
    toastMsg.textContent = message;
    const toast = new bootstrap.Toast(toastEl, { delay: 3500 });
    toast.show();
}

// Setup Role-Specific Navigation Links
function setupNavigation() {
    const badge = document.getElementById('navUserBadge');
    const roleColors = {
        FARMER: 'bg-success',
        CUSTOMER: 'bg-primary',
        DONOR: 'bg-danger',
        ADMIN: 'bg-secondary'
    };
    badge.className = `badge ${roleColors[currentUser.role] || 'bg-success'} text-white px-3 py-2 fw-semibold`;
    badge.innerHTML = `<i class="bi bi-person-circle"></i> ${escapeHtml(currentUser.name)} (${currentUser.role})`;

    const nav = document.getElementById('roleNavLinks');
    nav.innerHTML = '';

    if (currentUser.role === 'FARMER') {
        nav.innerHTML = `
            <li class="nav-item"><a class="nav-link agri-nav-link active" href="javascript:void(0)" onclick="switchTab(this, renderFarmerDashboard)"><i class="bi bi-speedometer2"></i> Dashboard</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerFarm)"><i class="bi bi-tree"></i> Farm Details</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerProducts)"><i class="bi bi-basket"></i> My Produce</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerOrders)"><i class="bi bi-box-seam"></i> Orders</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerEquipment)"><i class="bi bi-truck"></i> Equipment</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerDisease)"><i class="bi bi-virus text-warning"></i> AI Crop Doctor</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerWeather)"><i class="bi bi-cloud-sun"></i> Weather</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerFertilizer)"><i class="bi bi-droplet-half"></i> Fertilizer</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerMarket)"><i class="bi bi-graph-up"></i> Market MSP</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerGrievances)"><i class="bi bi-chat-heart"></i> Grievances</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerRatings)"><i class="bi bi-star"></i> Reviews</a></li>
        `;
    } else if (currentUser.role === 'CUSTOMER') {
        nav.innerHTML = `
            <li class="nav-item"><a class="nav-link agri-nav-link active" href="javascript:void(0)" onclick="switchTab(this, renderCustomerMarket)"><i class="bi bi-shop"></i> Produce Marketplace</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderCustomerCart)"><i class="bi bi-cart3"></i> Cart <span id="cartCountBadge" class="badge bg-danger rounded-pill">0</span></a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderCustomerOrders)"><i class="bi bi-box2"></i> My Orders & Tracking</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderCustomerEquipment)"><i class="bi bi-truck"></i> Machinery Rentals</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderFarmerMarket)"><i class="bi bi-graph-up"></i> Mandi Rates</a></li>
        `;
        updateCartBadge();
    } else if (currentUser.role === 'DONOR') {
        nav.innerHTML = `
            <li class="nav-item"><a class="nav-link agri-nav-link active" href="javascript:void(0)" onclick="switchTab(this, renderDonorGrievances)"><i class="bi bi-heart-pulse"></i> Farmer Support Grievances</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderDonorHistory)"><i class="bi bi-clock-history"></i> My Donation History</a></li>
        `;
    } else if (currentUser.role === 'ADMIN') {
        nav.innerHTML = `
            <li class="nav-item"><a class="nav-link agri-nav-link active" href="javascript:void(0)" onclick="switchTab(this, renderAdminOverview)"><i class="bi bi-speedometer2"></i> Admin Overview</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderAdminUsers)"><i class="bi bi-people"></i> Users</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderAdminProducts)"><i class="bi bi-basket"></i> Products</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderAdminOrders)"><i class="bi bi-receipt"></i> Orders</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderAdminGrievances)"><i class="bi bi-chat-heart"></i> Grievances</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderAdminDonations)"><i class="bi bi-cash-stack"></i> Donations</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderAdminEquipment)"><i class="bi bi-truck"></i> Equipment</a></li>
            <li class="nav-item"><a class="nav-link agri-nav-link" href="javascript:void(0)" onclick="switchTab(this, renderAdminReports)"><i class="bi bi-bar-chart"></i> Reports</a></li>
        `;
    }
}

function switchTab(el, callback) {
    document.querySelectorAll('.agri-nav-link').forEach(link => link.classList.remove('active'));
    if (el) el.classList.add('active');
    callback();
}

function loadRoleDefaultView() {
    if (currentUser.role === 'FARMER') renderFarmerDashboard();
    else if (currentUser.role === 'CUSTOMER') renderCustomerMarketplace();
    else if (currentUser.role === 'DONOR') renderDonorGrievances();
    else if (currentUser.role === 'ADMIN') renderAdminOverview();
}

// ---------------------------------------------------
// Customer Marketplace & Nearby Implementations (Phase 3)
// ---------------------------------------------------

/**
 * Render the Customer Marketplace page.
 * Loads the static HTML file and initializes UI logic.
 */
async function renderCustomerMarketplace() {
  const container = document.getElementById('workspaceContent');
  FarmerAPI.showLoading(true);
  try {
    const resp = await fetch('customer-marketplace.html');
    const html = await resp.text();
    container.innerHTML = html;
    initMarketplace(); // set up search, filters, load data
  } catch (e) {
    console.error(e);
    FarmerAPI.showAlert('danger', 'Failed to load marketplace UI');
    container.innerHTML = '<p class="text-danger">Unable to load marketplace.</p>';
  } finally {
    FarmerAPI.showLoading(false);
  }
}

/**
 * Render the Nearby Products page.
 * If the user lacks coordinates, shows an informative message.
 */
async function renderCustomerNearby() {
  const container = document.getElementById('workspaceContent');
  const user = AgriAuth.getUser();
  if (!user || user.latitude == null || user.longitude == null) {
    container.innerHTML = `<div class="alert alert-warning mt-3">
      Please complete your profile with a valid location (address/coordinates) to use the Nearby feature.
    </div>`;
    return;
  }
  FarmerAPI.showLoading(true);
  try {
    const resp = await fetch('customer-nearby.html');
    const html = await resp.text();
    container.innerHTML = html;
    initNearby(user.id);
  } catch (e) {
    console.error(e);
    FarmerAPI.showAlert('danger', 'Failed to load nearby UI');
  } finally {
    FarmerAPI.showLoading(false);
  }
}

/** Helper to refresh the cart count badge after an add‑to‑cart action */
async function refreshCartBadge() {
  const user = AgriAuth.getUser();
  if (!user) return;
  try {
    const resp = await FarmerAPI.get(`/api/cart/${user.id}`);
    if (resp.ok && resp.data && typeof resp.data.totalItems === 'number') {
      const badge = document.getElementById('cartCountBadge');
      if (badge) badge.textContent = resp.data.totalItems;
    }
  } catch (e) {
    console.warn('Could not refresh cart badge', e);
  }
}

/**
 * Initialize marketplace UI: fetch categories, bind events, load first batch.
 */
function initMarketplace() {
  const searchInput = document.getElementById('searchInput');
  const categorySelect = document.getElementById('categorySelect');
  const loadMoreBtn = document.getElementById('loadMoreBtn');

  let allProducts = [];
  let displayedCount = 0;
  const PAGE_SIZE = 12;
  let debounceTimer;

  // Load categories dynamically from product list
  async function loadCategories() {
    try {
      const resp = await FarmerAPI.get('/api/products');
      if (resp.ok) {
        const cats = new Set();
        resp.data.forEach(p => {
          if (p.category) cats.add(p.category);
        });
        const select = categorySelect;
        select.innerHTML = '<option value="">All Categories</option>';
        Array.from(cats).sort().forEach(c => {
          const opt = document.createElement('option');
          opt.value = c;
          opt.textContent = c;
          select.appendChild(opt);
        });
      }
    } catch (e) {
      console.warn('Failed to load categories', e);
    }
  }

  async function loadProducts(params = {}) {
    const qs = new URLSearchParams(params).toString();
    const resp = await FarmerAPI.get(`/api/products${qs ? '?' + qs : ''}`);
    if (resp.ok) {
      allProducts = resp.data;
      displayedCount = 0;
      renderNextBatch();
    } else {
      FarmerAPI.showAlert('danger', resp.data.error || 'Failed to load products');
    }
  }

  function renderNextBatch() {
    const grid = document.getElementById('productGrid');
    const slice = allProducts.slice(displayedCount, displayedCount + PAGE_SIZE);
    slice.forEach(p => {
      const card = document.createElement('div');
      card.className = 'col-md-4 col-lg-3 mb-4';
      card.innerHTML = `
        <div class="card h-100">
          <img src="${p.imageUrl || 'https://via.placeholder.com/150'}" class="card-img-top" alt="${p.name}">
          <div class="card-body d-flex flex-column">
            <h5 class="card-title fw-bold">${p.name}</h5>
            <p class="card-text text-muted mb-1">${p.category || ''}</p>
            <p class="card-text fw-bold text-success mb-2">₹${p.price} / ${p.unit}</p>
            <div class="mt-auto">
              <button class="btn btn-sm btn-outline-primary me-2" onclick="showProductDetails(${p.id})"><i class="bi bi-eye"></i> Details</button>
              <button class="btn btn-sm btn-success" onclick="addToCart(${p.id})"><i class="bi bi-cart-plus"></i> Add</button>
            </div>
          </div>
        </div>`;
      grid.appendChild(card);
    });
    displayedCount += slice.length;
    loadMoreBtn.style.display = displayedCount < allProducts.length ? 'block' : 'none';
  }

  // Debounced search / filter handler
  function triggerSearch() {
    const query = searchInput.value.trim();
    const category = categorySelect.value;
    const params = {};
    if (category) params.category = category;
    if (query) params.query = query;
    loadProducts(params);
  }

  // Event bindings
  searchInput.addEventListener('keyup', () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(triggerSearch, 300);
  });
  categorySelect.addEventListener('change', triggerSearch);
  loadMoreBtn.addEventListener('click', renderNextBatch);

  // Initial load
  loadCategories();
  loadProducts();
}

/** Show product details in a modal */
async function showProductDetails(productId) {
  try {
    const resp = await FarmerAPI.get(`/api/products/${productId}`);
    if (!resp.ok) { FarmerAPI.showAlert('danger', 'Failed to load product details'); return; }
    const p = resp.data;
    // Attempt to load review summary
    let ratingHtml = '<span class="text-muted small">No reviews yet</span>';
    try {
      const sumResp = await FarmerAPI.get(`/api/reviews/product/${productId}/summary`);
      if (sumResp.ok && sumResp.data) {
        const avg = sumResp.data.averageRating || 0;
        const count = sumResp.data.totalReviews || sumResp.data.totalReviewCount || 0;
        ratingHtml = `
          <div class="d-flex align-items-center gap-2 mb-2">
            <span class="badge bg-warning text-dark fw-bold"><i class="bi bi-star-fill"></i> ${avg.toFixed(1)}</span>
            <span class="text-muted small">(${count} review${count === 1 ? '' : 's'})</span>
            <a href="customer-reviews.html?productId=${p.id}" class="small text-success text-decoration-none fw-semibold ms-1">View Reviews</a>
          </div>`;
      }
    } catch (_) {}

    const modalBody = document.getElementById('productDetailBody');
    modalBody.innerHTML = `
      <div class="row g-3">
        <div class="col-md-5">
          <img src="${p.imageUrl || 'https://via.placeholder.com/300'}" class="img-fluid rounded" alt="${p.name}">
        </div>
        <div class="col-md-7">
          <h4 class="fw-bold mb-1">${p.name}</h4>
          ${ratingHtml}
          <p class="text-muted">${p.description || ''}</p>
          <p><strong>Category:</strong> ${p.category || ''}</p>
          <p><strong>Price:</strong> ₹${p.price} per ${p.unit}</p>
          <p><strong>Available:</strong> ${p.available ? 'Yes' : 'No'}</p>
          <p><strong>Farmer:</strong> ${p.farmerName || ''}<br>
             <strong>Location:</strong> ${p.farmerAddress || ''}</p>
          ${p.distanceKm != null ? `<p><strong>Distance:</strong> ${p.distanceKm} km</p>` : ''}
        </div>
      </div>`;
    const modal = new bootstrap.Modal(document.getElementById('productDetailModal'));
    modal.show();
  } catch (e) {
    console.error(e);
    FarmerAPI.showAlert('danger', 'Error loading product details');
  }
}

/** Add a product to the cart and refresh the badge */
async function addToCart(productId) {
  const user = AgriAuth.getUser();
  if (!user) { FarmerAPI.showAlert('danger', 'User not authenticated'); return; }
  try {
    const resp = await FarmerAPI.post('/api/cart', { customerId: user.id, productId, quantity: 1 });
    if (resp.ok) {
      FarmerAPI.showAlert('success', 'Item added to cart');
      await refreshCartBadge();
    } else {
      FarmerAPI.showAlert('danger', resp.data.error || 'Failed to add to cart');
    }
  } catch (e) {
    console.error(e);
    FarmerAPI.showAlert('danger', 'Error adding to cart');
  }
}

/** Initialize Nearby UI (similar to marketplace but with radius selector) */
function initNearby(customerId) {
  const radiusSelect = document.getElementById('radiusSelect');
  const loadMoreBtn = document.getElementById('loadMoreBtn');
  const grid = document.getElementById('productGrid');
  let allProducts = [];
  let displayedCount = 0;
  const PAGE_SIZE = 12;

  async function loadNearby() {
    const radius = radiusSelect.value || 50;
    const resp = await FarmerAPI.get(`/api/products/nearby?customerId=${customerId}&radiusKm=${radius}`);
    if (resp.ok) {
      allProducts = resp.data;
      displayedCount = 0;
      grid.innerHTML = '';
      renderNextBatch();
    } else {
      FarmerAPI.showAlert('danger', resp.data.error || 'Failed to load nearby products');
    }
  }

  function renderNextBatch() {
    const slice = allProducts.slice(displayedCount, displayedCount + PAGE_SIZE);
    slice.forEach(p => {
      const card = document.createElement('div');
      card.className = 'col-md-4 col-lg-3 mb-4';
      card.innerHTML = `
        <div class="card h-100">
          <img src="${p.imageUrl || 'https://via.placeholder.com/150'}" class="card-img-top" alt="${p.name}">
          <div class="card-body d-flex flex-column">
            <h5 class="card-title fw-bold">${p.name}</h5>
            <p class="card-text text-muted mb-1">${p.category || ''}</p>
            <p class="card-text fw-bold text-success mb-2">₹${p.price} / ${p.unit}</p>
            <p class="small text-muted">${p.distanceKm} km away</p>
            <div class="mt-auto">
              <button class="btn btn-sm btn-outline-primary me-2" onclick="showProductDetails(${p.id})"><i class="bi bi-eye"></i> Details</button>
              <button class="btn btn-sm btn-success" onclick="addToCart(${p.id})"><i class="bi bi-cart-plus"></i> Add</button>
            </div>
          </div>
        </div>`;
      grid.appendChild(card);
    });
    displayedCount += slice.length;
    loadMoreBtn.style.display = displayedCount < allProducts.length ? 'block' : 'none';
  }

  radiusSelect.addEventListener('change', loadNearby);
  loadMoreBtn.addEventListener('click', renderNextBatch);

  // Set default radius to 50km
  radiusSelect.value = '50';
  loadNearby();
}

// ---------------------------------------------------
// End of Phase 3 additions
// ---------------------------------------------------

// ---------------------------------------------------
// Phase 4: Customer Cart + Checkout — navigation shims
// ---------------------------------------------------

/**
 * Navigate to the standalone Customer Cart page.
 * Called from action cards, nav links, and anywhere else that
 * needs to open the cart. All cart logic lives in customer-cart.html.
 */
function renderCustomerCart() {
    window.location.href = 'customer-cart.html';
}

/**
 * Navigate to the standalone Customer Checkout page.
 * All checkout + mock-payment logic lives in customer-checkout.html.
 * This is only a navigation shim — no checkout logic is duplicated here.
 */
function renderCustomerCheckout() {
    window.location.href = 'customer-checkout.html';
}

/**
 * Navigate to the standalone Customer Orders page.
 * All orders logic lives in customer-orders.html.
 * This is only a navigation shim — no orders logic is duplicated here.
 */
function renderCustomerOrders() {
    window.location.href = 'customer-orders.html';
}

/**
 * Navigate to the standalone Customer Order Tracking page.
 * All tracking logic lives in customer-order-tracking.html.
 * This is only a navigation shim — no tracking logic is duplicated here.
 */
function renderCustomerOrderTracking(orderId) {
    if (orderId) {
        window.location.href = `customer-order-tracking.html?id=${orderId}`;
    } else {
        window.location.href = 'customer-orders.html';
    }
}

/**
 * Navigate to the standalone Customer Reviews page.
 * All reviews logic lives in customer-reviews.html.
 * This is only a navigation shim — no reviews logic is duplicated here.
 */
function renderCustomerReviews(productId) {
    if (productId) {
        window.location.href = `customer-reviews.html?productId=${productId}`;
    } else {
        window.location.href = 'customer-reviews.html';
    }
}

/**
 * Alias kept for any legacy call-sites that reference updateCartBadge().
 * Delegates to the existing refreshCartBadge() so there is no duplicate logic.
 */
function updateCartBadge() {
    refreshCartBadge();
}

// ---------------------------------------------------
// End of Phase 4 additions
// ---------------------------------------------------

// ---------------------------------------------------
// Fertilizer Recommendation — navigation shim
// All fertilizer logic lives in fertilizer.html.
// ---------------------------------------------------
/**
 * Navigate to the standalone Fertilizer Recommendation page.
 * Called from the legacy Farmer nav tab in setupNavigation().
 * No fertilizer logic is duplicated here.
 */
function renderFarmerFertilizer() {
    window.location.href = 'fertilizer.html';
}

// ---------------------------------------------------
// End of Fertilizer shim
// ---------------------------------------------------

function escapeHtml(str) {
    return String(str || '').replace(/[&<>'"]/g, tag => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'
    }[tag] || tag));
}

// ==========================================
// 1. FARMER MODULE
// ==========================================

async function renderFarmerDashboard() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/farmers/summary/${currentUser.id}`);
        const s = await res.json();

        c.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="fw-bold text-dark m-0">🌾 Welcome, ${escapeHtml(currentUser.name)}</h2>
                    <p class="text-muted small m-0">Farm Management & Agricultural Operation Center</p>
                </div>
                <div class="d-flex gap-2">
                    <button class="btn btn-agri-primary btn-sm" onclick="renderFarmerProducts()"><i class="bi bi-plus-circle"></i> Add Produce</button>
                    <button class="btn btn-warning btn-sm fw-semibold" onclick="renderFarmerDisease()"><i class="bi bi-virus"></i> AI Crop Doctor</button>
                </div>
            </div>

            <!-- KPI Cards -->
            <div class="row g-3 mb-4">
                <div class="col-md-4 col-lg-2">
                    <div class="agri-card p-3 stat-card h-100">
                        <div class="stat-label">Produce Listed</div>
                        <div class="stat-value">${s.totalProducts || 0}</div>
                        <small class="text-muted">Items for sale</small>
                    </div>
                </div>
                <div class="col-md-4 col-lg-2">
                    <div class="agri-card p-3 stat-card h-100" style="border-left-color: #3b82f6;">
                        <div class="stat-label">Total Orders</div>
                        <div class="stat-value text-primary">${s.totalOrders || 0}</div>
                        <small class="text-muted">Received</small>
                    </div>
                </div>
                <div class="col-md-4 col-lg-2">
                    <div class="agri-card p-3 stat-card h-100" style="border-left-color: #f59e0b;">
                        <div class="stat-label">Pending Orders</div>
                        <div class="stat-value text-warning">${s.pendingOrders || 0}</div>
                        <small class="text-muted">To be shipped</small>
                    </div>
                </div>
                <div class="col-md-4 col-lg-2">
                    <div class="agri-card p-3 stat-card h-100" style="border-left-color: #10b981;">
                        <div class="stat-label">Equipment</div>
                        <div class="stat-value text-success">${s.equipmentCount || 0}</div>
                        <small class="text-muted">Rentals active</small>
                    </div>
                </div>
                <div class="col-md-4 col-lg-2">
                    <div class="agri-card p-3 stat-card h-100" style="border-left-color: #ef4444;">
                        <div class="stat-label">Distress Support</div>
                        <div class="stat-value text-danger">₹${s.donationsReceived || 0}</div>
                        <small class="text-muted">${s.grievanceCount || 0} Grievances</small>
                    </div>
                </div>
                <div class="col-md-4 col-lg-2">
                    <div class="agri-card p-3 stat-card h-100" style="border-left-color: #8b5cf6;">
                        <div class="stat-label">Farmer Rating</div>
                        <div class="stat-value text-purple">${s.averageRating || 5.0} <i class="bi bi-star-fill text-warning fs-6"></i></div>
                        <small class="text-muted">${s.totalRatings || 0} reviews</small>
                    </div>
                </div>
            </div>

            <!-- Quick Action Shortcuts -->
            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="agri-card h-100">
                        <div class="agri-card-header">
                            <h5 class="agri-card-title"><i class="bi bi-grid-fill text-success"></i> Farming Operations & Advisory</h5>
                        </div>
                        <div class="agri-card-body">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <div class="p-3 border rounded-3 bg-light h-100">
                                        <h6 class="fw-bold text-dark"><i class="bi bi-virus text-warning"></i> AI Rice Leaf Disease Doctor</h6>
                                        <p class="small text-muted mb-2">Analyze crop photos with fine-tuned YOLO11n AI to classify leaf spot, blast, blight or scald.</p>
                                        <button class="btn btn-outline-success btn-sm w-100" onclick="renderFarmerDisease()">Open AI Doctor</button>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="p-3 border rounded-3 bg-light h-100">
                                        <h6 class="fw-bold text-dark"><i class="bi bi-droplet-half text-primary"></i> Fertilizer Recommendation</h6>
                                        <p class="small text-muted mb-2">Calculate balanced basal and split dosages of Urea, DAP, and MOP customized to your acreage and soil.</p>
                                        <button class="btn btn-outline-primary btn-sm w-100" onclick="renderFarmerFertilizer()">Calculate Doses</button>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="p-3 border rounded-3 bg-light h-100">
                                        <h6 class="fw-bold text-dark"><i class="bi bi-graph-up text-success"></i> Mandi & MSP Price Trends</h6>
                                        <p class="small text-muted mb-2">Check minimum support prices and daily terminal market rates for paddy, wheat, and pulses.</p>
                                        <button class="btn btn-outline-success btn-sm w-100" onclick="renderFarmerMarket()">View Prices</button>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="p-3 border rounded-3 bg-light h-100">
                                        <h6 class="fw-bold text-dark"><i class="bi bi-chat-heart text-danger"></i> Community Distress Grievance</h6>
                                        <p class="small text-muted mb-2">Post support requests for flood, drought, or pest damages to receive direct donor assistance.</p>
                                        <button class="btn btn-outline-danger btn-sm w-100" onclick="renderFarmerGrievances()">View Grievances</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Weather Quick Glance -->
                <div class="col-lg-4">
                    <div class="agri-card h-100">
                        <div class="agri-card-header">
                            <h5 class="agri-card-title"><i class="bi bi-cloud-sun text-warning"></i> Weather Forecast</h5>
                        </div>
                        <div class="agri-card-body" id="farmerDashWeather">
                            <div class="text-center py-3">
                                <div class="spinner-border spinner-border-sm text-success"></div> Loading weather...
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        loadQuickWeather();
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error loading dashboard: ${e.message}</div>`;
    }
}

async function loadQuickWeather() {
    const box = document.getElementById('farmerDashWeather');
    if (!box) return;
    try {
        const res = await fetch(`/api/weather?location=${encodeURIComponent(currentUser.address || 'Coimbatore')}`);
        const w = await res.json();
        box.innerHTML = `
            <div class="text-center mb-3">
                <div class="display-5 fw-bold text-dark">${w.temperature}°C</div>
                <div class="text-success fw-semibold text-capitalize">${escapeHtml(w.description)}</div>
                <div class="small text-muted">${escapeHtml(w.location)}</div>
            </div>
            <div class="d-flex justify-content-around bg-light p-2 rounded-3 small mb-3">
                <div><i class="bi bi-droplet"></i> ${w.humidity}% Hum</div>
                <div><i class="bi bi-wind"></i> ${w.windSpeed} m/s</div>
                <div><i class="bi bi-thermometer-half"></i> ${w.feelsLike || w.temperature}°C</div>
            </div>
            <div class="alert alert-success p-2 small m-0 border-0">
                <strong>Advisory:</strong> ${escapeHtml(w.advisory)}
            </div>
        `;
    } catch (e) {
        box.innerHTML = `<div class="text-muted small">Weather unavailable.</div>`;
    }
}

// ------------------------------------------
// 1.1 Farm Details
// ------------------------------------------
async function renderFarmerFarm() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/farms/${currentUser.id}`);
        const farm = await res.json();

        c.innerHTML = `
            <div class="agri-card max-w-700 mx-auto" style="max-width: 800px;">
                <div class="agri-card-header">
                    <h5 class="agri-card-title"><i class="bi bi-tree-fill text-success"></i> Manage Farm Information</h5>
                </div>
                <div class="agri-card-body">
                    <form onsubmit="saveFarmDetails(event)">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold small text-muted">Total Land Area</label>
                                <input type="number" step="0.1" id="farmArea" class="form-control" value="${farm.landArea || ''}" required placeholder="e.g. 3.5">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold small text-muted">Land Measurement Unit</label>
                                <select id="farmUnit" class="form-select">
                                    <option value="acres" ${farm.landUnit === 'acres' ? 'selected' : ''}>Acres</option>
                                    <option value="hectares" ${farm.landUnit === 'hectares' ? 'selected' : ''}>Hectares</option>
                                    <option value="cents" ${farm.landUnit === 'cents' ? 'selected' : ''}>Cents</option>
                                </select>
                            </div>
                            <div class="col-12">
                                <label class="form-label fw-semibold small text-muted">Farm Location / Village / Tehsil</label>
                                <input type="text" id="farmLocation" class="form-control" value="${escapeHtml(farm.location || currentUser.address || '')}" placeholder="e.g. Pollachi Taluk, Coimbatore">
                            </div>
                            <div class="col-12">
                                <label class="form-label fw-semibold small text-muted">Crops Grown</label>
                                <input type="text" id="farmCrops" class="form-control" value="${escapeHtml(farm.crops || '')}" placeholder="e.g. Rice (Paddy), Tomato, Groundnut">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold small text-muted">Dominant Soil Type</label>
                                <select id="farmSoil" class="form-select">
                                    <option value="Alluvial Loam" ${farm.soilType === 'Alluvial Loam' ? 'selected' : ''}>Alluvial Loam</option>
                                    <option value="Black Cotton Clay" ${farm.soilType === 'Black Cotton Clay' ? 'selected' : ''}>Black Cotton Clay</option>
                                    <option value="Red Laterite" ${farm.soilType === 'Red Laterite' ? 'selected' : ''}>Red Laterite</option>
                                    <option value="Sandy Loam" ${farm.soilType === 'Sandy Loam' ? 'selected' : ''}>Sandy Loam</option>
                                    <option value="Clayey" ${farm.soilType === 'Clayey' ? 'selected' : ''}>Clayey</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold small text-muted">Primary Irrigation System</label>
                                <select id="farmIrrigation" class="form-select">
                                    <option value="Drip Irrigation" ${farm.irrigationType === 'Drip Irrigation' ? 'selected' : ''}>Drip Irrigation</option>
                                    <option value="Canal / Flood Irrigation" ${farm.irrigationType === 'Canal / Flood Irrigation' ? 'selected' : ''}>Canal / Flood Irrigation</option>
                                    <option value="Sprinkler System" ${farm.irrigationType === 'Sprinkler System' ? 'selected' : ''}>Sprinkler System</option>
                                    <option value="Borewell Tube" ${farm.irrigationType === 'Borewell Tube' ? 'selected' : ''}>Borewell Tube</option>
                                    <option value="Rainfed" ${farm.irrigationType === 'Rainfed' ? 'selected' : ''}>Rainfed</option>
                                </select>
                            </div>
                        </div>
                        <div class="mt-4 text-end">
                            <button type="submit" class="btn btn-agri-primary"><i class="bi bi-save"></i> Save Farm Details</button>
                        </div>
                    </form>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function saveFarmDetails(e) {
    e.preventDefault();
    const data = {
        landArea: parseFloat(document.getElementById('farmArea').value),
        landUnit: document.getElementById('farmUnit').value,
        location: document.getElementById('farmLocation').value,
        crops: document.getElementById('farmCrops').value,
        soilType: document.getElementById('farmSoil').value,
        irrigationType: document.getElementById('farmIrrigation').value
    };

    try {
        const res = await fetch(`/api/farms/${currentUser.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error('Failed to update farm details');
        showToast('Farm details saved successfully!');
    } catch (err) {
        showToast(err.message, true);
    }
}

// ------------------------------------------
// 1.2 Produce / Product Management
// ------------------------------------------
async function renderFarmerProducts() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/products/farmer/${currentUser.id}`);
        const products = await res.json();

        let rows = '';
        if (products.length === 0) {
            rows = `<tr><td colspan="7" class="text-center py-4 text-muted">No agricultural produce listed yet. Click "Add Produce" to list your harvest for customers.</td></tr>`;
        } else {
            rows = products.map(p => `
                <tr>
                    <td>
                        <img src="${p.imageUrl || 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=100&auto=format&fit=crop'}"
                             style="width: 50px; height: 50px; object-fit: cover; border-radius: 8px;">
                    </td>
                    <td>
                        <div class="fw-bold text-dark">${escapeHtml(p.name)}</div>
                        <small class="text-muted">${escapeHtml(p.description || '')}</small>
                    </td>
                    <td><span class="badge bg-light text-dark border">${escapeHtml(p.category || 'Grains')}</span></td>
                    <td class="fw-bold text-success">₹${p.price} / ${escapeHtml(p.unit)}</td>
                    <td>
                        <span class="badge ${p.quantity > 0 ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                            ${p.quantity} ${escapeHtml(p.unit)}
                        </span>
                    </td>
                    <td>
                        <span class="badge ${p.available ? 'bg-success' : 'bg-secondary'}">
                            ${p.available ? 'Available' : 'Out of Stock'}
                        </span>
                    </td>
                    <td>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-outline-primary" onclick="openEditProductModal(${JSON.stringify(p).replace(/"/g, '&quot;')})"><i class="bi bi-pencil"></i></button>
                            <button class="btn btn-outline-danger" onclick="deleteProduct(${p.id})"><i class="bi bi-trash"></i></button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        c.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h3 class="fw-bold text-dark m-0">🥕 My Farm Produce Catalog</h3>
                    <p class="text-muted small m-0">List and manage crops directly available for customer purchase</p>
                </div>
                <button class="btn btn-agri-primary" onclick="openAddProductModal()"><i class="bi bi-plus-lg"></i> Add New Produce</button>
            </div>

            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>Photo</th>
                                <th>Crop / Produce</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

function openAddProductModal() {
    const mc = document.getElementById('modalContainer');
    mc.innerHTML = `
        <div class="modal fade" id="prodModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title fw-bold"><i class="bi bi-basket-fill"></i> Add Agricultural Produce</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form onsubmit="submitAddProduct(event)">
                        <div class="modal-body p-4">
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Produce Name *</label>
                                <input type="text" id="addProdName" class="form-control" placeholder="e.g. Organic Basmati Paddy" required>
                            </div>
                            <div class="row g-2 mb-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold small text-muted">Category *</label>
                                    <select id="addProdCat" class="form-select" required>
                                        <option value="Grains">Grains</option>
                                        <option value="Vegetables">Vegetables</option>
                                        <option value="Fruits">Fruits</option>
                                        <option value="Pulses">Pulses</option>
                                        <option value="Oil Seeds">Oil Seeds</option>
                                        <option value="Other">Other</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold small text-muted">Unit *</label>
                                    <select id="addProdUnit" class="form-select" required>
                                        <option value="kg">kg</option>
                                        <option value="quintal">quintal</option>
                                        <option value="crate">crate</option>
                                        <option value="bag">bag</option>
                                        <option value="ton">ton</option>
                                    </select>
                                </div>
                            </div>
                            <div class="row g-2 mb-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold small text-muted">Price (₹ per unit) *</label>
                                    <input type="number" step="0.5" id="addProdPrice" class="form-control" placeholder="45" required min="1">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold small text-muted">Available Quantity *</label>
                                    <input type="number" step="1" id="addProdQty" class="form-control" placeholder="100" required min="1">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Description</label>
                                <textarea id="addProdDesc" class="form-control" rows="2" placeholder="Freshly harvested, sun-dried, chemical free..."></textarea>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Image URL (Optional)</label>
                                <input type="url" id="addProdImg" class="form-control" placeholder="https://example.com/photo.jpg">
                            </div>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-agri-primary">List for Sale</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;
    const m = new bootstrap.Modal(document.getElementById('prodModal'));
    m.show();
}

async function submitAddProduct(e) {
    e.preventDefault();
    const data = {
        farmerId: currentUser.id,
        name: document.getElementById('addProdName').value.trim(),
        category: document.getElementById('addProdCat').value,
        unit: document.getElementById('addProdUnit').value,
        price: parseFloat(document.getElementById('addProdPrice').value),
        quantity: parseFloat(document.getElementById('addProdQty').value),
        description: document.getElementById('addProdDesc').value.trim(),
        imageUrl: document.getElementById('addProdImg').value.trim()
    };

    try {
        const res = await fetch('/api/products', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error('Failed to add product');
        bootstrap.Modal.getInstance(document.getElementById('prodModal')).hide();
        showToast('Produce listed successfully!');
        renderFarmerProducts();
    } catch (err) {
        showToast(err.message, true);
    }
}

function openEditProductModal(p) {
    const mc = document.getElementById('modalContainer');
    mc.innerHTML = `
        <div class="modal fade" id="editModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square"></i> Update Produce</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form onsubmit="submitEditProduct(event, ${p.id})">
                        <div class="modal-body p-4">
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Produce Name</label>
                                <input type="text" id="editName" class="form-control" value="${escapeHtml(p.name)}" required>
                            </div>
                            <div class="row g-2 mb-3">
                                <div class="col-6">
                                    <label class="form-label fw-semibold small text-muted">Price (₹ per ${p.unit})</label>
                                    <input type="number" step="0.5" id="editPrice" class="form-control" value="${p.price}" required min="1">
                                </div>
                                <div class="col-6">
                                    <label class="form-label fw-semibold small text-muted">Stock Quantity</label>
                                    <input type="number" step="0.5" id="editQty" class="form-control" value="${p.quantity}" required min="0">
                                </div>
                            </div>
                            <div class="mb-3">
                                <div class="form-check form-switch">
                                    <input class="form-check-input" type="checkbox" id="editAvailable" ${p.available ? 'checked' : ''}>
                                    <label class="form-check-label fw-semibold" for="editAvailable">Active for sale</label>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">Save Changes</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;
    const m = new bootstrap.Modal(document.getElementById('editModal'));
    m.show();
}

async function submitEditProduct(e, id) {
    e.preventDefault();
    const data = {
        name: document.getElementById('editName').value.trim(),
        price: parseFloat(document.getElementById('editPrice').value),
        quantity: parseFloat(document.getElementById('editQty').value),
        available: document.getElementById('editAvailable').checked
    };

    try {
        const res = await fetch(`/api/products/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error('Failed to update product');
        bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
        showToast('Produce updated!');
        renderFarmerProducts();
    } catch (err) {
        showToast(err.message, true);
    }
}

async function deleteProduct(id) {
    if (!confirm('Are you sure you want to remove this product listing?')) return;
    try {
        const res = await fetch(`/api/products/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete');
        showToast('Product removed.');
        renderFarmerProducts();
    } catch (err) {
        showToast(err.message, true);
    }
}

// ------------------------------------------
// 1.3 Farmer Orders Management
// ------------------------------------------

async function renderFarmerOrders() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/orders/farmer/${currentUser.id}`);
        const orders = await res.json();

        let rows = '';

        if (orders.length === 0) {
            rows = `<tr><td colspan="6" class="text-center py-4 text-muted">No orders received yet. New purchase orders will appear here.</td></tr>`;
        } else {
            rows = orders.map(o => {

                let nextStatus = null;
                let nextStatusLabel = null;

                switch (o.status) {
                    case 'PLACED':
                        nextStatus = 'CONFIRMED';
                        nextStatusLabel = 'Confirm Order';
                        break;

                    case 'CONFIRMED':
                        nextStatus = 'PACKED';
                        nextStatusLabel = 'Mark as Packed';
                        break;

                    case 'PACKED':
                        nextStatus = 'SHIPPED';
                        nextStatusLabel = 'Mark as Shipped';
                        break;

                    case 'SHIPPED':
                        nextStatus = 'OUT_FOR_DELIVERY';
                        nextStatusLabel = 'Out for Delivery';
                        break;

                    case 'OUT_FOR_DELIVERY':
                        nextStatus = 'DELIVERED';
                        nextStatusLabel = 'Mark as Delivered';
                        break;

                    default:
                        nextStatus = null;
                        nextStatusLabel = null;
                }

                const updateControl = nextStatus
                    ? `
                        <button
                            class="btn btn-success btn-sm"
                            onclick="updateOrderStatus(${o.id}, '${nextStatus}')">
                            ${nextStatusLabel}
                        </button>
                    `
                    : `
                        <span class="text-muted small">
                            ${o.status === 'DELIVERED' ? 'Order completed' : 'No updates'}
                        </span>
                    `;

                return `
                    <tr>
                        <td class="fw-bold">#${o.id}</td>

                        <td>
                            <div class="fw-semibold text-dark">
                                ${escapeHtml(o.customer ? o.customer.name : 'Customer')}
                            </div>

                            <small class="text-muted">
                                <i class="bi bi-geo-alt"></i>
                                ${escapeHtml(o.deliveryAddress || '')}
                            </small>
                        </td>

                        <td class="fw-bold text-success">
                            ₹${o.totalAmount}
                        </td>

                        <td>
                            <span class="badge ${getStatusBadge(o.status)}">
                                ${o.status}
                            </span>
                        </td>

                        <td class="small text-muted">
                            ${new Date(o.createdAt).toLocaleDateString()}
                        </td>

                        <td>
                            <div class="d-flex align-items-center gap-2">
                                ${updateControl}

                                <button
                                    class="btn btn-outline-info btn-sm"
                                    onclick="viewOrderItems(${o.id})">
                                    <i class="bi bi-eye"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                `;
            }).join('');
        }

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">📦 Customer Produce Orders</h3>
                <p class="text-muted small m-0">
                    Track and advance order delivery stages
                </p>
            </div>

            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>Order ID</th>
                                <th>Customer & Address</th>
                                <th>Amount</th>
                                <th>Current Status</th>
                                <th>Date</th>
                                <th>Update Progression</th>
                            </tr>
                        </thead>

                        <tbody>
                            ${rows}
                        </tbody>
                    </table>
                </div>
            </div>
        `;

    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}


async function updateOrderStatus(orderId, status) {
    try {
        if (!currentUser || currentUser.role !== 'FARMER') {
            showToast('Only farmers can update order status.', true);
            return;
        }

        const res = await fetch(
            `/api/orders/${orderId}/status?farmerId=${currentUser.id}`,
            {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ status })
            }
        );

        const data = await res.json();

        if (!res.ok) {
            throw new Error(data.error || 'Status update failed');
        }

        showToast(`Order #${orderId} marked as ${status}`);

        renderFarmerOrders();

    } catch (e) {
        showToast(e.message, true);
        renderFarmerOrders();
    }
}

async function viewOrderItems(orderId) {
    try {
        const res = await fetch(`/api/orders/${orderId}/items`);
        const items = await res.json();
        const mc = document.getElementById('modalContainer');
        mc.innerHTML = `
            <div class="modal fade" id="itemsModal" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content border-0 shadow">
                        <div class="modal-header bg-light">
                            <h5 class="modal-title fw-bold">Order #${orderId} Produce Breakdown</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body p-0">
                            <ul class="list-group list-group-flush">
                                ${items.map(it => `
                                    <li class="list-group-item d-flex justify-content-between align-items-center">
                                        <div>
                                            <div class="fw-bold text-dark">${escapeHtml(it.product.name)}</div>
                                            <small class="text-muted">${it.quantity} ${escapeHtml(it.product.unit)} @ ₹${it.unitPrice}</small>
                                        </div>
                                        <span class="fw-bold text-success">₹${(it.quantity * it.unitPrice).toFixed(2)}</span>
                                    </li>
                                `).join('')}
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        `;
        new bootstrap.Modal(document.getElementById('itemsModal')).show();
    } catch (e) {
        showToast(e.message, true);
    }
}

function getStatusBadge(st) {
    switch (st) {
        case 'PLACED': return 'bg-secondary';
        case 'CONFIRMED': return 'bg-info text-dark';
        case 'PACKED': return 'bg-primary';
        case 'SHIPPED': return 'bg-warning text-dark';
        case 'OUT_FOR_DELIVERY': return 'bg-warning-subtle text-warning border border-warning';
        case 'DELIVERED': return 'bg-success';
        case 'CANCELLED': return 'bg-danger';
        default: return 'bg-light text-dark';
    }
}

// ------------------------------------------
// 1.4 Equipment Rental
// ------------------------------------------
async function renderFarmerEquipment() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/equipment/owner/${currentUser.id}`);
        const list = await res.json();

        let rows = '';
        if (list.length === 0) {
            rows = `<tr><td colspan="6" class="text-center py-4 text-muted">No agricultural equipment listed yet. Click "Add Equipment" to rent out your tractors, tillers, or sprayers.</td></tr>`;
        } else {
            rows = list.map(e => `
                <tr>
                    <td class="fw-bold text-dark">${escapeHtml(e.name)}</td>
                    <td><span class="badge bg-light text-dark border">${escapeHtml(e.type || 'Machinery')}</span></td>
                    <td class="fw-bold text-success">₹${e.rentalPricePerDay} / day</td>
                    <td><i class="bi bi-geo-alt"></i> ${escapeHtml(e.location || 'Local')}</td>
                    <td>
                        <span class="badge ${e.available ? 'bg-success' : 'bg-secondary'}">
                            ${e.available ? 'Available' : 'Rented / Busy'}
                        </span>
                    </td>
                    <td>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-outline-primary" onclick="toggleEquipmentAvail(${e.id}, ${!e.available})">
                                ${e.available ? 'Mark Busy' : 'Mark Available'}
                            </button>
                            <button class="btn btn-outline-danger" onclick="deleteEquipment(${e.id})"><i class="bi bi-trash"></i></button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        c.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h3 class="fw-bold text-dark m-0">🚜 Farm Equipment & Machinery Listings</h3>
                    <p class="text-muted small m-0">Share and monetize idle farm equipment with neighboring growers</p>
                </div>
                <button class="btn btn-agri-primary" onclick="openAddEquipmentModal()"><i class="bi bi-plus-lg"></i> List Equipment</button>
            </div>

            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>Equipment Name</th>
                                <th>Type</th>
                                <th>Rental Price</th>
                                <th>Location</th>
                                <th>Availability</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

function openAddEquipmentModal() {
    const mc = document.getElementById('modalContainer');
    mc.innerHTML = `
        <div class="modal fade" id="eqModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title fw-bold"><i class="bi bi-truck"></i> List Agricultural Equipment</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form onsubmit="submitAddEquipment(event)">
                        <div class="modal-body p-4">
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Equipment Name *</label>
                                <input type="text" id="addEqName" class="form-control" placeholder="e.g. 25HP 4WD Tractor with Rotavator" required>
                            </div>
                            <div class="row g-2 mb-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold small text-muted">Equipment Type *</label>
                                    <select id="addEqType" class="form-select" required>
                                        <option value="Mini Tractor">Mini Tractor</option>
                                        <option value="Power Tiller">Power Tiller</option>
                                        <option value="Sprayer">Sprayer</option>
                                        <option value="Cultivator">Cultivator</option>
                                        <option value="Harvester">Harvester</option>
                                        <option value="Other">Other</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold small text-muted">Rental Price (₹ / day) *</label>
                                    <input type="number" id="addEqRent" class="form-control" placeholder="1500" required min="100">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Location / Tehsil *</label>
                                <input type="text" id="addEqLoc" class="form-control" value="${escapeHtml(currentUser.address || '')}" placeholder="e.g. Coimbatore Rural" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Description / Specifications</label>
                                <textarea id="addEqDesc" class="form-control" rows="2" placeholder="Diesel driven, includes operator option, suitable for paddy..."></textarea>
                            </div>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-agri-primary">List Equipment</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;
    new bootstrap.Modal(document.getElementById('eqModal')).show();
}

async function submitAddEquipment(e) {
    e.preventDefault();
    const data = {
        ownerId: currentUser.id,
        name: document.getElementById('addEqName').value.trim(),
        type: document.getElementById('addEqType').value,
        rentalPricePerDay: parseFloat(document.getElementById('addEqRent').value),
        location: document.getElementById('addEqLoc').value.trim(),
        description: document.getElementById('addEqDesc').value.trim()
    };

    try {
        const res = await fetch('/api/equipment', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error('Failed to list equipment');
        bootstrap.Modal.getInstance(document.getElementById('eqModal')).hide();
        showToast('Equipment listed successfully!');
        renderFarmerEquipment();
    } catch (err) {
        showToast(err.message, true);
    }
}

async function toggleEquipmentAvail(id, avail) {
    try {
        const res = await fetch(`/api/equipment/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ available: avail })
        });
        if (!res.ok) throw new Error('Update failed');
        renderFarmerEquipment();
    } catch (e) {
        showToast(e.message, true);
    }
}

async function deleteEquipment(id) {
    if (!confirm('Remove this equipment listing?')) return;
    try {
        await fetch(`/api/equipment/${id}`, { method: 'DELETE' });
        showToast('Equipment removed');
        renderFarmerEquipment();
    } catch (e) {
        showToast(e.message, true);
    }
}

// ------------------------------------------
// 1.5 REAL AI CROP DISEASE DETECTION
// ------------------------------------------
function renderFarmerDisease() {
    window.location.href = 'crop-disease.html';
}

function renderCropDisease() {
    window.location.href = 'crop-disease.html';
}


// ------------------------------------------
// 1.6 Fertilizer Recommendation
// ------------------------------------------
function renderFarmerFertilizer() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = `
        <div class="row g-4">
            <div class="col-lg-5">
                <div class="agri-card h-100">
                    <div class="agri-card-header">
                        <h5 class="agri-card-title"><i class="bi bi-droplet-half text-primary"></i> Fertilizer Calculator</h5>
                    </div>
                    <div class="agri-card-body">
                        <form onsubmit="calculateFertilizer(event)">
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Crop</label>
                                <select id="fertCrop" class="form-select">
                                    <option value="Rice (Paddy)">Rice (Paddy)</option>
                                    <option value="Wheat">Wheat</option>
                                    <option value="Tomato">Tomato</option>
                                    <option value="Maize">Maize</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Soil Type</label>
                                <select id="fertSoil" class="form-select">
                                    <option value="Alluvial Loam">Alluvial Loam</option>
                                    <option value="Black Cotton Clay">Black Cotton Clay</option>
                                    <option value="Red Laterite">Red Laterite</option>
                                    <option value="Sandy Loam">Sandy Loam</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Land Area (Acres)</label>
                                <input type="number" step="0.5" id="fertArea" class="form-control" value="2.0" min="0.5" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Crop Stage</label>
                                <select id="fertStage" class="form-select">
                                    <option value="Basal / Field Preparation">Basal / Field Preparation</option>
                                    <option value="Active Tillering">Active Tillering</option>
                                    <option value="Panicle Emergence">Panicle Emergence</option>
                                    <option value="Grain Filling">Grain Filling</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-agri-primary w-100"><i class="bi bi-calculator"></i> Calculate Recommendation</button>
                        </form>
                    </div>
                </div>
            </div>

            <div class="col-lg-7">
                <div class="agri-card h-100">
                    <div class="agri-card-header">
                        <h5 class="agri-card-title"><i class="bi bi-card-checklist text-success"></i> Agronomic Advisory</h5>
                    </div>
                    <div class="agri-card-body" id="fertResultBox">
                        <div class="agri-empty-state py-4">
                            <i class="bi bi-droplet"></i>
                            <h4>Fertilizer Dosages</h4>
                            <p class="small">Enter your crop and acreage parameters on the left to generate balanced split dosages.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

async function calculateFertilizer(e) {
    e.preventDefault();
    const crop = document.getElementById('fertCrop').value;
    const soil = document.getElementById('fertSoil').value;
    const area = document.getElementById('fertArea').value;
    const stage = document.getElementById('fertStage').value;

    const box = document.getElementById('fertResultBox');
    box.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/fertilizer/recommendation?crop=${encodeURIComponent(crop)}&soilType=${encodeURIComponent(soil)}&landArea=${area}&stage=${encodeURIComponent(stage)}`);
        const d = await res.json();

        box.innerHTML = `
            <div class="mb-3">
                <span class="badge bg-success-subtle text-success me-1">${d.crop}</span>
                <span class="badge bg-light text-dark border me-1">${d.soilType}</span>
                <span class="badge bg-light text-dark border">${d.landArea}</span>
            </div>

            <h6 class="fw-bold text-dark mb-2">Recommended Nutrient Quantities</h6>
            <div class="list-group mb-3">
                ${d.recommendations.map(r => `
                    <div class="list-group-item d-flex justify-content-between align-items-center">
                        <div>
                            <div class="fw-bold text-dark">${escapeHtml(r.name)}</div>
                            <small class="text-muted">${escapeHtml(r.timing)}</small>
                        </div>
                        <span class="badge bg-success fs-6">${escapeHtml(r.dose)}</span>
                    </div>
                `).join('')}
            </div>

            <div class="mb-3">
                <h6 class="fw-bold text-dark small text-uppercase">Application Guidance</h6>
                <p class="small text-muted bg-light p-2 rounded mb-0">${escapeHtml(d.guidance)}</p>
            </div>

            <div class="mb-3">
                <h6 class="fw-bold text-dark small text-uppercase">Precautions</h6>
                <p class="small text-muted bg-light p-2 rounded mb-0">${escapeHtml(d.precautions)}</p>
            </div>

            <div class="alert alert-secondary p-2 small m-0 fst-italic">
                <i class="bi bi-info-circle"></i> ${escapeHtml(d.disclaimer)}
            </div>
        `;
    } catch (err) {
        box.innerHTML = `<div class="alert alert-danger">${err.message}</div>`;
    }
}

// ------------------------------------------
// 1.7 Weather Advisory
// ------------------------------------------
function renderFarmerWeather() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = `
        <div class="agri-card max-w-700 mx-auto" style="max-width: 750px;">
            <div class="agri-card-header">
                <h5 class="agri-card-title"><i class="bi bi-cloud-sun text-warning"></i> OpenWeatherMap Meteorological Advisory</h5>
            </div>
            <div class="agri-card-body">
                <div class="input-group mb-4">
                    <input type="text" id="weatherSearchLoc" class="form-control" placeholder="Enter City / District / State (e.g. Coimbatore, Thanjavur, Delhi)" value="${escapeHtml(currentUser.address ? currentUser.address.split(',')[0] : 'Coimbatore')}">
                    <button class="btn btn-agri-primary" onclick="searchWeather()"><i class="bi bi-search"></i> Check Weather</button>
                </div>
                <div id="weatherFullResult">
                    <div class="text-center py-4"><div class="spinner-border text-success"></div></div>
                </div>
            </div>
        </div>
    `;
    searchWeather();
}

async function searchWeather() {
    const loc = document.getElementById('weatherSearchLoc').value.trim();
    const box = document.getElementById('weatherFullResult');
    box.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/weather?location=${encodeURIComponent(loc)}`);
        const w = await res.json();

        if (!res.ok || w.status === 'error') {
            box.innerHTML = `
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                    ${escapeHtml(w.message || 'Unable to retrieve weather data for "' + loc + '".')}
                </div>
                <button class="btn btn-outline-danger btn-sm mt-2" onclick="searchWeather()">
                    <i class="bi bi-arrow-clockwise"></i> Retry
                </button>`;
            return;
        }

        box.innerHTML = `
            <div class="p-4 rounded-3 text-white text-center mb-4" style="background: linear-gradient(135deg, #1b4332 0%, #2d6a4f 100%);">
                <h3 class="fw-bold mb-1">${escapeHtml(w.location)}</h3>
                <div class="display-3 fw-bolder my-2">${w.temperature}°C</div>
                <div class="lead text-light text-capitalize">${escapeHtml(w.description)}</div>
                <div class="small text-white-50">Feels like ${w.feelsLike != null ? w.feelsLike : w.temperature}°C • Barometer: ${w.pressure != null ? w.pressure : '--'} hPa</div>
            </div>

            <div class="row g-3 text-center mb-4">
                <div class="col-4">
                    <div class="p-3 bg-light rounded-3">
                        <div class="text-muted small">Relative Humidity</div>
                        <div class="fs-4 fw-bold text-primary"><i class="bi bi-droplet-fill"></i> ${w.humidity != null ? w.humidity : '--'}%</div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="p-3 bg-light rounded-3">
                        <div class="text-muted small">Wind Velocity</div>
                        <div class="fs-4 fw-bold text-success"><i class="bi bi-wind"></i> ${w.windSpeed != null ? w.windSpeed : '--'} m/s</div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="p-3 bg-light rounded-3">
                        <div class="text-muted small">Condition</div>
                        <div class="fs-4 fw-bold text-dark"><i class="bi bi-cloud-sun-fill text-warning"></i> ${escapeHtml(w.condition || 'Fair')}</div>
                    </div>
                </div>
            </div>

            <div class="alert alert-success border-0 p-3 m-0">
                <h6 class="fw-bold mb-1"><i class="bi bi-shield-check"></i> Agronomic Advisory</h6>
                <p class="small mb-0">${escapeHtml(w.advisory || 'Conditions are stable.')}</p>
            </div>
        `;
    } catch (e) {
        box.innerHTML = `
            <div class="alert alert-danger"><i class="bi bi-exclamation-triangle-fill"></i> ${escapeHtml(e.message)}</div>
            <button class="btn btn-outline-danger btn-sm mt-2" onclick="searchWeather()">
                <i class="bi bi-arrow-clockwise"></i> Retry
            </button>`;
    }
}

// ------------------------------------------
// 1.8 Market & MSP Prices
// ------------------------------------------
async function renderFarmerMarket() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/market-prices');
        const prices = await res.json();

        c.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h3 class="fw-bold text-dark m-0">💰 Market & MSP Reference Prices</h3>
                    <p class="text-muted small m-0">Official benchmark Minimum Support Prices and terminal APMC rates (Demo/Reference Dataset)</p>
                </div>
                <div class="input-group" style="max-width: 300px;">
                    <input type="text" id="marketSearch" class="form-control" placeholder="Search crop or mandi..." onkeyup="filterMarketTable(this.value)">
                    <span class="input-group-text bg-light"><i class="bi bi-search"></i></span>
                </div>
            </div>

            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0" id="marketTable">
                        <thead class="table-light">
                            <tr>
                                <th>Crop Commodity</th>
                                <th>Category</th>
                                <th>APMC Market</th>
                                <th>State</th>
                                <th>Benchmark Price</th>
                                <th>Effective Date</th>
                                <th>Type</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${prices.map(p => `
                                <tr>
                                    <td class="fw-bold text-dark">${escapeHtml(p.crop)}</td>
                                    <td><span class="badge bg-light text-dark border">${escapeHtml(p.category)}</span></td>
                                    <td>${escapeHtml(p.market)}</td>
                                    <td>${escapeHtml(p.state)}</td>
                                    <td class="fw-bold text-success">${escapeHtml(p.price)} / ${escapeHtml(p.unit)}</td>
                                    <td class="small text-muted">${escapeHtml(p.date)}</td>
                                    <td><span class="badge bg-success-subtle text-success">${escapeHtml(p.type)}</span></td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

function filterMarketTable(val) {
    const q = val.toLowerCase();
    document.querySelectorAll('#marketTable tbody tr').forEach(tr => {
        tr.style.display = tr.textContent.toLowerCase().includes(q) ? '' : 'none';
    });
}

// ------------------------------------------
// 1.9 Grievance Center
// ------------------------------------------
async function renderFarmerGrievances() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/grievances/farmer/${currentUser.id}`);
        const grievances = await res.json();

        let cards = '';
        if (grievances.length === 0) {
            cards = `
                <div class="col-12 text-center py-5 agri-empty-state">
                    <i class="bi bi-chat-heart"></i>
                    <h4>No Grievances Raised</h4>
                    <p class="small">If your crops suffered damages due to floods, drought, or pests, post a request to receive community assistance.</p>
                </div>
            `;
        } else {
            cards = grievances.map(g => {
                const target = g.targetAmount || 1;
                const rec = g.receivedAmount || 0;
                const pct = Math.min(100, Math.round((rec / target) * 100));
                return `
                    <div class="col-md-6">
                        <div class="agri-card h-100 p-4">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge bg-danger-subtle text-danger"><i class="bi bi-exclamation-circle"></i> ${escapeHtml(g.category)}</span>
                                <span class="badge ${g.status === 'FUNDED' ? 'bg-success' : 'bg-warning text-dark'}">${g.status}</span>
                            </div>
                            <h5 class="fw-bold text-dark">${escapeHtml(g.title)}</h5>
                            <p class="small text-muted mb-3">${escapeHtml(g.description)}</p>

                            <div class="mb-2">
                                <div class="d-flex justify-content-between small fw-semibold mb-1">
                                    <span>Funded: ₹${rec}</span>
                                    <span>Target: ₹${target} (${pct}%)</span>
                                </div>
                                <div class="progress" style="height: 10px;">
                                    <div class="progress-bar bg-success" role="progressbar" style="width: ${pct}%;"></div>
                                </div>
                            </div>
                            <div class="small text-muted mt-3">Posted on ${new Date(g.createdAt).toLocaleDateString()}</div>
                        </div>
                    </div>
                `;
            }).join('');
        }

        c.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h3 class="fw-bold text-dark m-0">🆘 Distress Grievance & Community Support</h3>
                    <p class="text-muted small m-0">Post natural catastrophe support requests and monitor community contributions</p>
                </div>
                <button class="btn btn-agri-primary" onclick="openCreateGrievanceModal()"><i class="bi bi-plus-circle"></i> Post New Grievance</button>
            </div>

            <div class="row g-3">${cards}</div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

function openCreateGrievanceModal() {
    const mc = document.getElementById('modalContainer');
    mc.innerHTML = `
        <div class="modal fade" id="grievModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title fw-bold"><i class="bi bi-heart-pulse-fill"></i> Raise Farmer Grievance</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form onsubmit="submitCreateGrievance(event)">
                        <div class="modal-body p-4">
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Title of Distress Request *</label>
                                <input type="text" id="grTitle" class="form-control" placeholder="e.g. Severe Flood Inundation in Paddy Fields" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Cause Category *</label>
                                <select id="grCat" class="form-select" required>
                                    <option value="Flood / Rain Damage">Flood / Rain Damage</option>
                                    <option value="Drought">Drought</option>
                                    <option value="Crop Disease">Crop Disease</option>
                                    <option value="Pest Damage">Pest Damage</option>
                                    <option value="Equipment Loss">Equipment Loss</option>
                                    <option value="Other">Other</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Financial Assistance Target (₹) *</label>
                                <input type="number" id="grTarget" class="form-control" placeholder="25000" min="500" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Detailed Description of Damage *</label>
                                <textarea id="grDesc" class="form-control" rows="3" placeholder="Explain the crop area damaged, estimated yield loss, and how the funds will assist in revival..." required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-danger fw-bold">Submit Request</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;
    new bootstrap.Modal(document.getElementById('grievModal')).show();
}

async function submitCreateGrievance(e) {
    e.preventDefault();
    const data = {
        farmerId: currentUser.id,
        title: document.getElementById('grTitle').value.trim(),
        category: document.getElementById('grCat').value,
        targetAmount: parseFloat(document.getElementById('grTarget').value),
        description: document.getElementById('grDesc').value.trim()
    };

    try {
        const res = await fetch('/api/grievances', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error('Failed to create grievance');
        bootstrap.Modal.getInstance(document.getElementById('grievModal')).hide();
        showToast('Grievance posted for community support!');
        renderFarmerGrievances();
    } catch (err) {
        showToast(err.message, true);
    }
}

// ------------------------------------------
// 1.10 Reviews & Ratings Received
// ------------------------------------------
async function renderFarmerRatings() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/ratings/farmer/${currentUser.id}`);
        const data = await res.json();

        let reviewsHtml = '';
        if (!data.reviews || data.reviews.length === 0) {
            reviewsHtml = `<div class="agri-empty-state py-4"><i class="bi bi-star"></i><h4>No Reviews Yet</h4><p class="small">Completed customer orders will appear here once rated.</p></div>`;
        } else {
            reviewsHtml = data.reviews.map(r => `
                <div class="p-3 border-bottom">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <div class="fw-bold text-dark">${escapeHtml(r.customer.name)}</div>
                        <div class="text-warning">
                            ${'★'.repeat(Math.round(r.averageScore))}${'☆'.repeat(5 - Math.round(r.averageScore))}
                            <span class="text-muted small ms-1">(${r.averageScore})</span>
                        </div>
                    </div>
                    <div class="d-flex gap-3 small text-muted mb-2">
                        <span>Produce Quality: <strong>${r.productQuality}/5</strong></span>
                        <span>Delivery Experience: <strong>${r.deliveryExperience}/5</strong></span>
                    </div>
                    <p class="small text-dark mb-0">${escapeHtml(r.comment || 'No written comment.')}</p>
                    <small class="text-muted">${new Date(r.createdAt).toLocaleDateString()}</small>
                </div>
            `).join('');
        }

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">⭐ Customer Ratings & Testimonials</h3>
                <p class="text-muted small m-0">Transparent buyer feedback for your farm produce and dispatch quality</p>
            </div>

            <div class="row g-4">
                <div class="col-md-4">
                    <div class="agri-card p-4 text-center">
                        <div class="display-3 fw-bolder text-warning">${data.averageRating || 5.0}</div>
                        <div class="fs-4 text-warning mb-2">
                            ${'★'.repeat(Math.round(data.averageRating || 5))}${'☆'.repeat(5 - Math.round(data.averageRating || 5))}
                        </div>
                        <div class="fw-bold text-dark">Overall Satisfaction</div>
                        <div class="small text-muted">Based on ${data.totalRatings || 0} verified customer reviews</div>
                    </div>
                </div>
                <div class="col-md-8">
                    <div class="agri-card">
                        <div class="agri-card-header">
                            <h5 class="agri-card-title"><i class="bi bi-chat-left-quote"></i> Customer Review Feed</h5>
                        </div>
                        <div class="agri-card-body p-0">${reviewsHtml}</div>
                    </div>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}


// ==========================================
// 2. CUSTOMER MODULE
// ==========================================

async function renderCustomerMarket(category = 'All', query = '') {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        let url = '/api/products';
        const params = [];
        if (category && category !== 'All') params.push(`category=${encodeURIComponent(category)}`);
        if (query) params.push(`query=${encodeURIComponent(query)}`);
        if (params.length > 0) url += '?' + params.join('&');

        const res = await fetch(url);
        const products = await res.json();

        const categories = ['All', 'Grains', 'Vegetables', 'Fruits', 'Pulses', 'Oil Seeds', 'Other'];
        const catButtons = categories.map(cat => `
            <button class="btn btn-sm ${cat === category ? 'btn-success fw-bold' : 'btn-outline-secondary'}" onclick="renderCustomerMarket('${cat}', '${escapeHtml(query)}')">
                ${cat}
            </button>
        `).join('');

        let cards = '';
        if (products.length === 0) {
            cards = `
                <div class="col-12 text-center py-5 agri-empty-state">
                    <i class="bi bi-basket3"></i>
                    <h4>No Produce Found</h4>
                    <p class="small">Try adjusting your search query or selecting a different produce category.</p>
                </div>
            `;
        } else {
            cards = products.map(p => `
                <div class="col-sm-6 col-lg-4 col-xl-3">
                    <div class="agri-card h-100 d-flex flex-column">
                        <img src="${p.imageUrl || 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop'}"
                             style="height: 180px; object-fit: cover;">
                        <div class="p-3 d-flex flex-column flex-grow-1">
                            <div class="d-flex justify-content-between align-items-center mb-1">
                                <span class="badge bg-light text-dark border small">${escapeHtml(p.category)}</span>
                                <span class="badge ${p.quantity > 0 ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                    ${p.quantity > 0 ? p.quantity + ' ' + escapeHtml(p.unit) + ' left' : 'Out of Stock'}
                                </span>
                            </div>
                            <h5 class="fw-bold text-dark mb-1">${escapeHtml(p.name)}</h5>
                            <p class="small text-muted mb-2 flex-grow-1">${escapeHtml(p.description || 'Farm-harvested produce.')}</p>

                            <div class="d-flex justify-content-between align-items-center small text-muted mb-3 border-top pt-2">
                                <span><i class="bi bi-person"></i> ${escapeHtml(p.farmer ? p.farmer.name : 'Local Farmer')}</span>
                                <span class="text-warning"><i class="bi bi-star-fill"></i> 5.0</span>
                            </div>

                            <div class="d-flex justify-content-between align-items-center mt-auto">
                                <span class="fs-5 fw-bold text-success">₹${p.price} <small class="text-muted fs-6">/${escapeHtml(p.unit)}</small></span>
                                <button class="btn btn-agri-primary btn-sm" ${p.quantity <= 0 ? 'disabled' : ''} onclick="addToCart(${p.id}, 1)">
                                    <i class="bi bi-cart-plus"></i> Add to Cart
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `).join('');
        }

        c.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h3 class="fw-bold text-dark m-0">🛒 Direct Farmer Produce Marketplace</h3>
                    <p class="text-muted small m-0">Fresh farm harvests direct from regional growers with zero middlemen</p>
                </div>
                <div class="input-group" style="max-width: 320px;">
                    <input type="text" id="prodSearchInput" class="form-control" placeholder="Search crops, produce..." value="${escapeHtml(query)}" onkeydown="if(event.key==='Enter') renderCustomerMarket('${category}', this.value)">
                    <button class="btn btn-agri-primary" onclick="renderCustomerMarket('${category}', document.getElementById('prodSearchInput').value)"><i class="bi bi-search"></i></button>
                </div>
            </div>

            <!-- Categories -->
            <div class="d-flex gap-2 flex-wrap mb-4">${catButtons}</div>

            <!-- Produce Cards Grid -->
            <div class="row g-4">${cards}</div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function addToCart(productId, qty = 1) {
    try {
        const res = await fetch(`/api/cart/${currentUser.id}/items`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId, quantity: qty })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to add item to cart');
        showToast('Produce added to cart!');
        updateCartBadge();
    } catch (err) {
        showToast(err.message, true);
    }
}

async function updateCartBadge() {
    const badge = document.getElementById('cartCountBadge');
    if (!badge || currentUser.role !== 'CUSTOMER') return;
    try {
        const res = await fetch(`/api/cart/${currentUser.id}`);
        const items = await res.json();
        badge.textContent = items.length || 0;
    } catch (e) {}
}

async function renderCustomerCart() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/cart/${currentUser.id}`);
        const items = await res.json();

        if (items.length === 0) {
            c.innerHTML = `
                <div class="agri-card p-5 text-center max-w-700 mx-auto">
                    <i class="bi bi-cart-x display-3 text-muted mb-3 d-block"></i>
                    <h3 class="fw-bold text-dark">Your Cart is Empty</h3>
                    <p class="text-muted">Browse our marketplace and add fresh produce directly from local farmers.</p>
                    <button class="btn btn-agri-primary" onclick="renderCustomerMarket()"><i class="bi bi-shop"></i> Browse Marketplace</button>
                </div>
            `;
            return;
        }

        let total = 0;
        const rows = items.map(it => {
            const p = it.product;
            const sub = it.quantity * p.price;
            total += sub;
            return `
                <tr>
                    <td>
                        <div class="fw-bold text-dark">${escapeHtml(p.name)}</div>
                        <small class="text-muted">Farmer: ${escapeHtml(p.farmer ? p.farmer.name : 'Farmer')} • ₹${p.price}/${p.unit}</small>
                    </td>
                    <td class="fw-bold text-success">₹${p.price}</td>
                    <td>
                        <div class="input-group input-group-sm" style="width: 120px;">
                            <button class="btn btn-outline-secondary" onclick="updateCartQty(${it.id}, ${it.quantity - 1})">-</button>
                            <span class="form-control text-center fw-bold">${it.quantity}</span>
                            <button class="btn btn-outline-secondary" onclick="updateCartQty(${it.id}, ${it.quantity + 1})">+</button>
                        </div>
                    </td>
                    <td class="fw-bold text-dark">₹${sub.toFixed(2)}</td>
                    <td>
                        <button class="btn btn-outline-danger btn-sm" onclick="removeCartItem(${it.id})"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `;
        }).join('');

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">🧺 Shopping Cart</h3>
                <p class="text-muted small m-0">Review items before proceeding to simulated payment</p>
            </div>

            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="agri-card">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle m-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>Produce</th>
                                        <th>Unit Price</th>
                                        <th>Quantity</th>
                                        <th>Subtotal</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>${rows}</tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="agri-card p-4">
                        <h5 class="fw-bold text-dark mb-3">Order Summary</h5>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Produce Subtotal</span>
                            <span class="fw-bold">₹${total.toFixed(2)}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Direct Farm Dispatch</span>
                            <span class="text-success fw-semibold">Free Delivery</span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between fs-5 fw-bold mb-4">
                            <span>Total Amount</span>
                            <span class="text-success">₹${total.toFixed(2)}</span>
                        </div>

                        <button class="btn btn-success w-100 py-2 fw-bold" onclick="openCheckoutModal(${JSON.stringify(items).replace(/"/g, '&quot;')}, ${total})">
                            <i class="bi bi-credit-card"></i> Proceed to Checkout
                        </button>
                        <button class="btn btn-outline-secondary w-100 mt-2 btn-sm" onclick="renderCustomerMarket()">
                            Continue Shopping
                        </button>
                    </div>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function updateCartQty(itemId, qty) {
    try {
        const res = await fetch(`/api/cart/${currentUser.id}/items/${itemId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quantity: qty })
        });
        if (!res.ok) {
            const d = await res.json();
            throw new Error(d.error || 'Update failed');
        }
        renderCustomerCart();
        updateCartBadge();
    } catch (e) {
        showToast(e.message, true);
    }
}

async function removeCartItem(itemId) {
    try {
        await fetch(`/api/cart/${currentUser.id}/items/${itemId}`, { method: 'DELETE' });
        renderCustomerCart();
        updateCartBadge();
    } catch (e) {
        showToast(e.message, true);
    }
}

// ------------------------------------------
// 2.2 Checkout & Mock Payment
// ------------------------------------------
function openCheckoutModal(items, total) {
    const mc = document.getElementById('modalContainer');
    mc.innerHTML = `
        <div class="modal fade" id="chkModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title fw-bold"><i class="bi bi-shield-check"></i> Simulated Payment Gateway</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form onsubmit="processOrderCheckout(event, ${total})">
                        <div class="modal-body p-4">
                            <div class="alert alert-info py-2 small mb-3">
                                <i class="bi bi-info-circle"></i> <strong>Simulation Gateway:</strong> Academic prototype. No real money or bank accounts will be charged.
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Delivery Address</label>
                                <textarea id="chkAddress" class="form-control" rows="2" required>${escapeHtml(currentUser.address || 'Coimbatore, Tamil Nadu')}</textarea>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Payment Method</label>
                                <div class="d-flex gap-2">
                                    <input type="radio" class="btn-check" name="chkMethod" id="mUpi" value="UPI" checked>
                                    <label class="btn btn-outline-success flex-fill" for="mUpi"><i class="bi bi-phone"></i> UPI (GPay)</label>

                                    <input type="radio" class="btn-check" name="chkMethod" id="mCard" value="CARD">
                                    <label class="btn btn-outline-success flex-fill" for="mCard"><i class="bi bi-credit-card"></i> Card</label>

                                    <input type="radio" class="btn-check" name="chkMethod" id="mNet" value="NET_BANKING">
                                    <label class="btn btn-outline-success flex-fill" for="mNet"><i class="bi bi-bank"></i> Net Banking</label>
                                </div>
                            </div>

                            <div class="p-3 bg-light rounded-3 d-flex justify-content-between align-items-center mb-3">
                                <span class="fw-bold">Total Payable:</span>
                                <span class="fs-4 fw-bolder text-success">₹${total.toFixed(2)}</span>
                            </div>

                            <div class="form-check mb-3">
                                <input class="form-check-input" type="checkbox" id="chkSimulateSuccess" checked>
                                <label class="form-check-label small" for="chkSimulateSuccess">
                                    Simulate Successful Authorization (Uncheck to test simulated decline)
                                </label>
                            </div>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" id="payBtn" class="btn btn-success fw-bold px-4">
                                Authorize & Place Order (₹${total.toFixed(2)})
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;
    new bootstrap.Modal(document.getElementById('chkModal')).show();
}

async function processOrderCheckout(e, total) {
    e.preventDefault();
    const btn = document.getElementById('payBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Processing Payment...';

    const address = document.getElementById('chkAddress').value.trim();
    const method = document.querySelector('input[name="chkMethod"]:checked').value;
    const simulateSuccess = document.getElementById('chkSimulateSuccess').checked;

    try {
        // 1. Process simulated payment
        const payRes = await fetch('/api/payments/process', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ amount: total, method, simulateSuccess })
        });
        const payData = await payRes.json();

        if (payData.status !== 'SUCCESS') {
            throw new Error(payData.message || 'Payment simulation failed/declined.');
        }

        // 2. Fetch current cart items to create order
        const cartRes = await fetch(`/api/cart/${currentUser.id}`);
        const cartItems = await cartRes.json();

        const orderPayload = {
            customerId: currentUser.id,
            deliveryAddress: address,
            deliveryType: 'Standard Farm Dispatch',
            items: cartItems.map(it => ({
                productId: it.product.id,
                quantity: it.quantity
            }))
        };

        const ordRes = await fetch('/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderPayload)
        });
        const ordData = await ordRes.json();

        if (!ordRes.ok) throw new Error(ordData.error || 'Failed to generate order');

        bootstrap.Modal.getInstance(document.getElementById('chkModal')).hide();
        showToast('Payment successful! Order #' + ordData.orderId + ' has been placed.');
        updateCartBadge();
        renderCustomerOrders();

    } catch (err) {
        showToast(err.message, true);
        btn.disabled = false;
        btn.innerHTML = 'Authorize & Place Order';
    }
}

// ------------------------------------------
// 2.3 Customer Order Tracking & Rating
// ------------------------------------------
async function renderCustomerOrders() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/orders/user/${currentUser.id}`);
        const orders = await res.json();

        let ordersHtml = '';
        if (orders.length === 0) {
            ordersHtml = `
                <div class="agri-card p-5 text-center agri-empty-state">
                    <i class="bi bi-box2"></i>
                    <h4>No Orders Placed Yet</h4>
                    <p class="small">Your purchased agricultural produce and delivery status will appear here.</p>
                    <button class="btn btn-agri-primary" onclick="renderCustomerMarket()">Shop Produce</button>
                </div>
            `;
        } else {
            ordersHtml = orders.map(o => `
                <div class="agri-card p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                        <div>
                            <h5 class="fw-bold text-dark m-0">Order #${o.id}</h5>
                            <small class="text-muted">Placed on ${new Date(o.createdAt).toLocaleDateString()} • Dispatch to: ${escapeHtml(o.deliveryAddress)}</small>
                        </div>
                        <div class="d-flex align-items-center gap-2">
                            <span class="fs-5 fw-bold text-success">₹${o.totalAmount}</span>
                            <span class="badge ${getStatusBadge(o.status)} fs-6">${o.status}</span>
                        </div>
                    </div>

                    <!-- 6-Stage Visual Stepper -->
                    ${renderOrderStepper(o.status)}

                    <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top flex-wrap gap-2">
                        <button class="btn btn-outline-info btn-sm" onclick="viewOrderItems(${o.id})"><i class="bi bi-list-check"></i> View Produce Items</button>
                        ${o.status === 'DELIVERED' ? `
                            <button class="btn btn-warning btn-sm fw-bold" onclick="openRateFarmerModal(${o.id})">
                                <i class="bi bi-star-fill"></i> Rate Farmer & Produce
                            </button>
                        ` : `
                            <span class="small text-muted"><i class="bi bi-hourglass-split"></i> Rating unlocked once order is DELIVERED</span>
                        `}
                    </div>
                </div>
            `).join('');
        }

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">📦 My Orders & Live Dispatch Tracking</h3>
                <p class="text-muted small m-0">Track farm packaging, shipping, and delivery milestones</p>
            </div>
            ${ordersHtml}
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

function renderOrderStepper(currentStatus) {
    const stages = ['PLACED', 'CONFIRMED', 'PACKED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED'];
    const currentIdx = stages.indexOf(currentStatus);

    const stepsHtml = stages.map((stage, idx) => {
        let cls = '';
        let icon = idx + 1;
        if (currentStatus === 'CANCELLED') {
            cls = 'cancelled';
        } else if (idx < currentIdx) {
            cls = 'completed';
            icon = '<i class="bi bi-check-lg"></i>';
        } else if (idx === currentIdx) {
            cls = 'active';
            icon = '<i class="bi bi-record-fill"></i>';
        }

        const labels = {
            'PLACED': 'Placed',
            'CONFIRMED': 'Confirmed',
            'PACKED': 'Packed',
            'SHIPPED': 'Shipped',
            'OUT_FOR_DELIVERY': 'Out for Delivery',
            'DELIVERED': 'Delivered'
        };

        return `
            <div class="stepper-step ${cls}">
                <div class="stepper-circle">${icon}</div>
                <div class="stepper-label">${labels[stage]}</div>
            </div>
        `;
    }).join('');

    return `<div class="order-stepper">${stepsHtml}</div>`;
}

async function openRateFarmerModal(orderId) {
    try {
        const res = await fetch(`/api/orders/${orderId}/items`);
        const items = await res.json();
        const farmer = items[0] && items[0].product ? items[0].product.farmer : null;
        const farmerId = farmer ? farmer.id : 1;
        const farmerName = farmer ? farmer.name : 'Farmer';

        const mc = document.getElementById('modalContainer');
        mc.innerHTML = `
            <div class="modal fade" id="rateModal" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content border-0 shadow-lg">
                        <div class="modal-header bg-warning text-dark">
                            <h5 class="modal-title fw-bold"><i class="bi bi-star-fill"></i> Review & Rate Farmer</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <form onsubmit="submitFarmerRating(event, ${orderId}, ${farmerId})">
                            <div class="modal-body p-4">
                                <div class="mb-3">
                                    <div class="fw-bold text-dark">Farmer: ${escapeHtml(farmerName)}</div>
                                    <small class="text-muted">Order #${orderId} Delivered</small>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-semibold small text-muted">Produce Quality (1 to 5 Stars)</label>
                                    <select id="rateQuality" class="form-select" required>
                                        <option value="5" selected>5 Stars - Exceptional & Fresh</option>
                                        <option value="4">4 Stars - Good Quality</option>
                                        <option value="3">3 Stars - Average Quality</option>
                                        <option value="2">2 Stars - Below Expectations</option>
                                        <option value="1">1 Star - Poor Quality</option>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-semibold small text-muted">Delivery & Packaging Experience</label>
                                    <select id="rateDelivery" class="form-select" required>
                                        <option value="5" selected>5 Stars - Prompt & Well Packaged</option>
                                        <option value="4">4 Stars - Satisfactory</option>
                                        <option value="3">3 Stars - Normal</option>
                                        <option value="2">2 Stars - Delayed</option>
                                        <option value="1">1 Star - Damaged Packaging</option>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-semibold small text-muted">Review Comment</label>
                                    <textarea id="rateComment" class="form-control" rows="2" placeholder="Write a short testimonial about the produce aroma, cleanliness, or taste..."></textarea>
                                </div>
                            </div>
                            <div class="modal-footer bg-light">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                <button type="submit" class="btn btn-warning fw-bold">Submit Review</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        `;
        new bootstrap.Modal(document.getElementById('rateModal')).show();
    } catch (e) {
        showToast(e.message, true);
    }
}

async function submitFarmerRating(e, orderId, farmerId) {
    e.preventDefault();
    const data = {
        customerId: currentUser.id,
        farmerId: farmerId,
        orderId: orderId,
        productQuality: parseInt(document.getElementById('rateQuality').value),
        deliveryExperience: parseInt(document.getElementById('rateDelivery').value),
        comment: document.getElementById('rateComment').value.trim()
    };

    try {
        const res = await fetch('/api/ratings', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        const resp = await res.json();
        if (!res.ok) throw new Error(resp.error || 'Failed to submit rating');

        bootstrap.Modal.getInstance(document.getElementById('rateModal')).hide();
        showToast('Thank you! Your review has been submitted.');
        renderCustomerOrders();
    } catch (err) {
        showToast(err.message, true);
    }
}

// ------------------------------------------
// 2.4 Equipment Rental for Customers
// ------------------------------------------
async function renderCustomerEquipment() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/equipment');
        const list = await res.json();

        let cards = '';
        if (list.length === 0) {
            cards = `<div class="col-12 text-center py-5 agri-empty-state"><i class="bi bi-truck"></i><h4>No Equipment Available</h4></div>`;
        } else {
            cards = list.map(e => `
                <div class="col-md-6 col-lg-4">
                    <div class="agri-card h-100 p-4 d-flex flex-column">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="badge bg-light text-dark border">${escapeHtml(e.type || 'Machinery')}</span>
                            <span class="badge bg-success">Available</span>
                        </div>
                        <h5 class="fw-bold text-dark mb-1">${escapeHtml(e.name)}</h5>
                        <p class="small text-muted mb-3 flex-grow-1">${escapeHtml(e.description || 'Reliable agricultural equipment.')}</p>
                        <div class="small text-muted mb-3"><i class="bi bi-geo-alt"></i> Location: ${escapeHtml(e.location)}</div>
                        <div class="d-flex justify-content-between align-items-center pt-2 border-top mt-auto">
                            <span class="fs-5 fw-bold text-success">₹${e.rentalPricePerDay} <small class="text-muted fs-6">/ day</small></span>
                            <button class="btn btn-outline-success btn-sm" onclick="showToast('Machinery contact request forwarded to owner!')">Rent Now</button>
                        </div>
                    </div>
                </div>
            `).join('');
        }

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">🚜 Agricultural Machinery Rentals</h3>
                <p class="text-muted small m-0">Rent tractors, sprayers, and tillers directly from verified farmers</p>
            </div>
            <div class="row g-4">${cards}</div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}


// ==========================================
// 3. DONOR MODULE
// ==========================================

async function renderDonorGrievances() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/grievances');
        const list = await res.json();

        let cards = '';
        if (list.length === 0) {
            cards = `<div class="col-12 text-center py-5 agri-empty-state"><i class="bi bi-heart-pulse"></i><h4>No Active Farmer Grievances</h4></div>`;
        } else {
            cards = list.map(g => {
                const target = g.targetAmount || 1;
                const rec = g.receivedAmount || 0;
                const pct = Math.min(100, Math.round((rec / target) * 100));
                const remaining = Math.max(0, target - rec);
                const isFunded = rec >= target;

                return `
                    <div class="col-md-6 col-lg-4">
                        <div class="agri-card h-100 p-4 d-flex flex-column">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge bg-danger-subtle text-danger"><i class="bi bi-exclamation-triangle"></i> ${escapeHtml(g.category)}</span>
                                <span class="badge ${isFunded ? 'bg-success' : 'bg-warning text-dark'}">${g.status}</span>
                            </div>
                            <h5 class="fw-bold text-dark mb-1">${escapeHtml(g.title)}</h5>
                            <div class="small text-muted mb-2"><i class="bi bi-person"></i> Farmer: ${escapeHtml(g.farmer ? g.farmer.name : 'Grower')}</div>
                            <p class="small text-muted mb-3 flex-grow-1">${escapeHtml(g.description)}</p>

                            <div class="mb-3">
                                <div class="d-flex justify-content-between small fw-semibold mb-1">
                                    <span class="text-success">Raised: ₹${rec}</span>
                                    <span class="text-muted">Target: ₹${target}</span>
                                </div>
                                <div class="progress" style="height: 8px;">
                                    <div class="progress-bar bg-success" style="width: ${pct}%;"></div>
                                </div>
                                <div class="small text-muted mt-1 text-end">${pct}% Funded</div>
                            </div>

                            <div class="mt-auto pt-2 border-top">
                                ${isFunded ? `
                                    <button class="btn btn-outline-success w-100 btn-sm disabled"><i class="bi bi-check2-circle"></i> Target Fully Funded</button>
                                ` : `
                                    <button class="btn btn-danger w-100 fw-bold btn-sm" onclick="openDonateModal(${g.id}, '${escapeHtml(g.title)}', '${escapeHtml(g.farmer ? g.farmer.name : 'Farmer')}', ${remaining})">
                                        <i class="bi bi-heart-fill"></i> Support this Farmer
                                    </button>
                                `}
                            </div>
                        </div>
                    </div>
                `;
            }).join('');
        }

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">💚 Farmer Relief & Distress Grievances</h3>
                <p class="text-muted small m-0">Direct financial solidarity to smallholder farmers recovering from natural disasters</p>
            </div>
            <div class="row g-4">${cards}</div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

function openDonateModal(grievanceId, title, farmerName, remaining) {
    const mc = document.getElementById('modalContainer');
    mc.innerHTML = `
        <div class="modal fade" id="donModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title fw-bold"><i class="bi bi-heart-pulse-fill"></i> Community Distress Contribution</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form onsubmit="submitDonation(event, ${grievanceId})">
                        <div class="modal-body p-4">
                            <div class="alert alert-info py-2 small mb-3">
                                <strong>Simulated Payment:</strong> Prototype demonstration. No financial charge will occur.
                            </div>

                            <div class="mb-3">
                                <div class="fw-bold text-dark">${escapeHtml(title)}</div>
                                <small class="text-muted">Beneficiary: ${escapeHtml(farmerName)}</small>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Donation Amount (₹) *</label>
                                <input type="number" id="donAmount" class="form-control form-control-lg text-success fw-bold" value="${Math.min(1000, remaining || 500)}" min="100" max="${Math.max(100, remaining)}" required>
                                <small class="text-muted">Unfunded balance remaining: ₹${remaining}</small>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-semibold small text-muted">Simulated Payment Method</label>
                                <select id="donMethod" class="form-select">
                                    <option value="UPI">UPI (Google Pay / PhonePe)</option>
                                    <option value="CARD">Credit / Debit Card</option>
                                    <option value="NET_BANKING">Net Banking</option>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" id="donSubmitBtn" class="btn btn-danger fw-bold px-4">
                                Complete Simulated Donation
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;
    new bootstrap.Modal(document.getElementById('donModal')).show();
}

async function submitDonation(e, grievanceId) {
    e.preventDefault();
    const btn = document.getElementById('donSubmitBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Authorizing...';

    const amount = parseFloat(document.getElementById('donAmount').value);

    try {
        const res = await fetch('/api/donations', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                donorId: currentUser.id,
                grievanceId: grievanceId,
                amount: amount
            })
        });
        const d = await res.json();
        if (!res.ok) throw new Error(d.error || 'Donation failed');

        bootstrap.Modal.getInstance(document.getElementById('donModal')).hide();
        showToast('Donation successful! Receipt #' + d.receiptNumber);
        renderDonorGrievances();
    } catch (err) {
        showToast(err.message, true);
        btn.disabled = false;
        btn.innerHTML = 'Complete Simulated Donation';
    }
}

async function renderDonorHistory() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch(`/api/donations/donor/${currentUser.id}`);
        const history = await res.json();

        let rows = '';
        if (history.length === 0) {
            rows = `<tr><td colspan="5" class="text-center py-4 text-muted">You haven't made any donations yet.</td></tr>`;
        } else {
            rows = history.map(d => `
                <tr>
                    <td class="fw-bold text-danger">${escapeHtml(d.receiptNumber || ('REC-' + d.id))}</td>
                    <td>
                        <div class="fw-semibold text-dark">${escapeHtml(d.grievance ? d.grievance.title : 'Relief Fund')}</div>
                        <small class="text-muted">Farmer: ${escapeHtml(d.grievance && d.grievance.farmer ? d.grievance.farmer.name : 'Grower')}</small>
                    </td>
                    <td class="fw-bold text-success fs-6">₹${d.amount}</td>
                    <td><span class="badge bg-success">${d.paymentStatus}</span></td>
                    <td class="small text-muted">${new Date(d.donatedAt).toLocaleDateString()}</td>
                </tr>
            `).join('');
        }

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">📜 My Community Donation History</h3>
                <p class="text-muted small m-0">Verified contribution receipts for tax deduction or recordkeeping</p>
            </div>

            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>Receipt Number</th>
                                <th>Grievance & Beneficiary</th>
                                <th>Amount Donated</th>
                                <th>Status</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}


// ==========================================
// 4. ADMIN MODULE
// ==========================================

async function renderAdminOverview() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/admin/stats');
        const s = await res.json();

        c.innerHTML = `
            <div class="mb-4">
                <h2 class="fw-bold text-dark m-0">⚙️ AgriConnect Administrative Console</h2>
                <p class="text-muted small m-0">System performance, participant audit, and marketplace moderation</p>
            </div>

            <div class="row g-3 mb-4">
                <div class="col-sm-6 col-lg-3">
                    <div class="agri-card p-3 stat-card">
                        <div class="stat-label">Total Users</div>
                        <div class="stat-value text-dark">${s.totalUsers || 0}</div>
                        <small class="text-muted">${s.totalFarmers || 0} Farmers • ${s.totalCustomers || 0} Customers</small>
                    </div>
                </div>
                <div class="col-sm-6 col-lg-3">
                    <div class="agri-card p-3 stat-card" style="border-left-color: #3b82f6;">
                        <div class="stat-label">Produce Listings</div>
                        <div class="stat-value text-primary">${s.totalProducts || 0}</div>
                        <small class="text-muted">Active marketplace goods</small>
                    </div>
                </div>
                <div class="col-sm-6 col-lg-3">
                    <div class="agri-card p-3 stat-card" style="border-left-color: #10b981;">
                        <div class="stat-label">Total Orders</div>
                        <div class="stat-value text-success">${s.totalOrders || 0}</div>
                        <small class="text-muted">Purchases fulfilled</small>
                    </div>
                </div>
                <div class="col-sm-6 col-lg-3">
                    <div class="agri-card p-3 stat-card" style="border-left-color: #ef4444;">
                        <div class="stat-label">Relief Funds Raised</div>
                        <div class="stat-value text-danger">₹${s.totalDonationAmount || 0}</div>
                        <small class="text-muted">${s.totalDonations || 0} Donations • ${s.totalGrievances || 0} Grievances</small>
                    </div>
                </div>
            </div>

            <div class="row g-4">
                <div class="col-md-6">
                    <div class="agri-card p-4 h-100">
                        <h5 class="fw-bold text-dark mb-3"><i class="bi bi-people-fill text-primary"></i> User Role Distribution</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span>🌾 Farmers</span> <span class="badge bg-success fs-6">${s.totalFarmers || 0}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span>🛒 Customers</span> <span class="badge bg-primary fs-6">${s.totalCustomers || 0}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span>💚 Donors</span> <span class="badge bg-danger fs-6">${s.totalDonors || 0}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span>⚙️ Administrators</span> <span class="badge bg-secondary fs-6">1</span>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="agri-card p-4 h-100">
                        <h5 class="fw-bold text-dark mb-3"><i class="bi bi-sliders text-success"></i> Operational Shortcuts</h5>
                        <div class="d-grid gap-2">
                            <button class="btn btn-outline-primary" onclick="renderAdminUsers()"><i class="bi bi-people"></i> Manage User Accounts</button>
                            <button class="btn btn-outline-success" onclick="renderAdminProducts()"><i class="bi bi-basket"></i> Moderate Produce Catalog</button>
                            <button class="btn btn-outline-warning text-dark" onclick="renderAdminOrders()"><i class="bi bi-receipt"></i> Inspect Order Dispatches</button>
                            <button class="btn btn-outline-danger" onclick="renderAdminGrievances()"><i class="bi bi-chat-heart"></i> Oversee Relief Grievances</button>
                            <button class="btn btn-outline-secondary" onclick="renderAdminReports()"><i class="bi bi-bar-chart"></i> View Statistical Analytics</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function renderAdminUsers() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/admin/users');
        const users = await res.json();

        const rows = users.map(u => `
            <tr>
                <td class="fw-bold text-dark">${u.id}</td>
                <td class="fw-semibold">${escapeHtml(u.name)}</td>
                <td>${escapeHtml(u.email)}</td>
                <td>${escapeHtml(u.phone || 'N/A')}</td>
                <td><span class="badge ${u.role === 'FARMER' ? 'bg-success' : u.role === 'CUSTOMER' ? 'bg-primary' : u.role === 'DONOR' ? 'bg-danger' : 'bg-secondary'}">${u.role}</span></td>
                <td class="small text-muted">${escapeHtml(u.address || 'N/A')}</td>
            </tr>
        `).join('');

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">👥 Platform User Accounts</h3>
                <p class="text-muted small m-0">Registered farmers, customers, donors, and administrators</p>
            </div>
            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>User ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Role</th>
                                <th>Address</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function renderAdminProducts() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/admin/products');
        const products = await res.json();

        const rows = products.map(p => `
            <tr>
                <td class="fw-bold">${p.id}</td>
                <td class="fw-bold text-dark">${escapeHtml(p.name)}</td>
                <td><span class="badge bg-light text-dark border">${escapeHtml(p.category)}</span></td>
                <td>${escapeHtml(p.farmer ? p.farmer.name : 'Farmer')}</td>
                <td class="fw-bold text-success">₹${p.price} / ${escapeHtml(p.unit)}</td>
                <td>${p.quantity} ${escapeHtml(p.unit)}</td>
                <td>
                    <button class="btn btn-outline-danger btn-sm" onclick="adminDeleteProduct(${p.id})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `).join('');

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">📦 Marketplace Produce Moderation</h3>
                <p class="text-muted small m-0">Audit listed crops and remove unauthorized entries</p>
            </div>
            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Produce Name</th>
                                <th>Category</th>
                                <th>Farmer</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function adminDeleteProduct(id) {
    if (!confirm('Admin: remove this produce from the platform?')) return;
    try {
        await fetch(`/api/products/${id}`, { method: 'DELETE' });
        showToast('Produce listing removed');
        renderAdminProducts();
    } catch (e) {
        showToast(e.message, true);
    }
}

async function renderAdminOrders() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/admin/orders');
        const orders = await res.json();

        const rows = orders.map(o => `
            <tr>
                <td class="fw-bold">#${o.id}</td>
                <td>${escapeHtml(o.customer ? o.customer.name : 'Customer')}</td>
                <td class="fw-bold text-success">₹${o.totalAmount}</td>
                <td><span class="badge ${getStatusBadge(o.status)}">${o.status}</span></td>
                <td><span class="badge bg-success">${o.paymentStatus}</span></td>
                <td class="small text-muted">${new Date(o.createdAt).toLocaleDateString()}</td>
                <td>
                    <button class="btn btn-outline-info btn-sm" onclick="viewOrderItems(${o.id})"><i class="bi bi-eye"></i></button>
                </td>
            </tr>
        `).join('');

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">🧾 Platform Orders Audit</h3>
                <p class="text-muted small m-0">All customer agricultural purchase orders</p>
            </div>
            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>Order ID</th>
                                <th>Customer</th>
                                <th>Total Amount</th>
                                <th>Fulfillment Status</th>
                                <th>Payment</th>
                                <th>Date</th>
                                <th>Details</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function renderAdminGrievances() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/admin/grievances');
        const list = await res.json();

        const rows = list.map(g => `
            <tr>
                <td class="fw-bold text-dark">${escapeHtml(g.title)}</td>
                <td>${escapeHtml(g.farmer ? g.farmer.name : 'Farmer')}</td>
                <td><span class="badge bg-danger-subtle text-danger">${escapeHtml(g.category)}</span></td>
                <td class="fw-bold">₹${g.targetAmount}</td>
                <td class="text-success fw-bold">₹${g.receivedAmount || 0}</td>
                <td><span class="badge ${g.status === 'FUNDED' ? 'bg-success' : 'bg-warning text-dark'}">${g.status}</span></td>
                <td class="small text-muted">${new Date(g.createdAt).toLocaleDateString()}</td>
            </tr>
        `).join('');

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">🆘 Relief Grievance Moderation</h3>
                <p class="text-muted small m-0">Distress assistance requests across all rural districts</p>
            </div>
            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>Grievance Title</th>
                                <th>Farmer</th>
                                <th>Cause</th>
                                <th>Target</th>
                                <th>Received</th>
                                <th>Status</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function renderAdminDonations() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/admin/donations');
        const list = await res.json();

        const rows = list.map(d => `
            <tr>
                <td class="fw-bold text-danger">${escapeHtml(d.receiptNumber || ('REC-' + d.id))}</td>
                <td>${escapeHtml(d.donor ? d.donor.name : 'Anonymous Donor')}</td>
                <td>${escapeHtml(d.grievance ? d.grievance.title : 'Relief')}</td>
                <td class="fw-bold text-success">₹${d.amount}</td>
                <td><span class="badge bg-success">${d.paymentStatus}</span></td>
                <td class="small text-muted">${new Date(d.donatedAt).toLocaleDateString()}</td>
            </tr>
        `).join('');

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">💚 Philanthropic Donations Ledger</h3>
                <p class="text-muted small m-0">Complete audit trail of community relief transactions</p>
            </div>
            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>Receipt</th>
                                <th>Donor</th>
                                <th>Beneficiary Grievance</th>
                                <th>Amount</th>
                                <th>Status</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function renderAdminEquipment() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/admin/equipment');
        const list = await res.json();

        const rows = list.map(e => `
            <tr>
                <td class="fw-bold">${e.id}</td>
                <td class="fw-bold text-dark">${escapeHtml(e.name)}</td>
                <td><span class="badge bg-light text-dark border">${escapeHtml(e.type)}</span></td>
                <td>${escapeHtml(e.owner ? e.owner.name : 'Farmer')}</td>
                <td class="fw-bold text-success">₹${e.rentalPricePerDay}/day</td>
                <td>${escapeHtml(e.location)}</td>
                <td><span class="badge ${e.available ? 'bg-success' : 'bg-secondary'}">${e.available ? 'Available' : 'Busy'}</span></td>
            </tr>
        `).join('');

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">🚜 Equipment Registry</h3>
                <p class="text-muted small m-0">All registered tractors, power tillers, and sprayers</p>
            </div>
            <div class="agri-card">
                <div class="table-responsive">
                    <table class="table table-hover align-middle m-0">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Type</th>
                                <th>Owner</th>
                                <th>Rate</th>
                                <th>Location</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}

async function renderAdminReports() {
    const c = document.getElementById('workspaceContent');
    c.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

    try {
        const res = await fetch('/api/admin/reports');
        const r = await res.json();

        c.innerHTML = `
            <div class="mb-4">
                <h3 class="fw-bold text-dark m-0">📊 Platform Analytical Reports</h3>
                <p class="text-muted small m-0">System breakdown metrics and aggregated performance statistics</p>
            </div>

            <div class="row g-4">
                <div class="col-md-6">
                    <div class="agri-card p-4 h-100">
                        <h5 class="fw-bold text-dark mb-3"><i class="bi bi-people"></i> Users by Role</h5>
                        <ul class="list-group list-group-flush">
                            ${Object.entries(r.usersByRole || {}).map(([role, count]) => `
                                <li class="list-group-item d-flex justify-content-between align-items-center">
                                    <span>${role}</span>
                                    <span class="badge bg-success fs-6">${count}</span>
                                </li>
                            `).join('')}
                        </ul>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="agri-card p-4 h-100">
                        <h5 class="fw-bold text-dark mb-3"><i class="bi bi-box-seam"></i> Orders by Status</h5>
                        <ul class="list-group list-group-flush">
                            ${Object.entries(r.ordersByStatus || {}).map(([st, count]) => `
                                <li class="list-group-item d-flex justify-content-between align-items-center">
                                    <span class="badge ${getStatusBadge(st)}">${st}</span>
                                    <span class="fw-bold">${count}</span>
                                </li>
                            `).join('')}
                        </ul>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="agri-card p-4 h-100">
                        <h5 class="fw-bold text-dark mb-3"><i class="bi bi-basket"></i> Produce by Category</h5>
                        <ul class="list-group list-group-flush">
                            ${Object.entries(r.productsByCategory || {}).map(([cat, count]) => `
                                <li class="list-group-item d-flex justify-content-between align-items-center">
                                    <span>${cat}</span>
                                    <span class="badge bg-primary fs-6">${count}</span>
                                </li>
                            `).join('')}
                        </ul>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="agri-card p-4 h-100">
                        <h5 class="fw-bold text-dark mb-3"><i class="bi bi-cash-coin text-danger"></i> Community Relief Capital</h5>
                        <div class="p-3 bg-light rounded-3 text-center mb-3">
                            <div class="text-muted small">Total Donations Collected</div>
                            <div class="display-5 fw-bolder text-danger">₹${r.totalDonations || 0}</div>
                        </div>
                        <p class="small text-muted mb-0">100% of community relief funds are distributed directly to smallholder farmers impacted by unseasonal floods, drought, or disease.</p>
                    </div>
                </div>
            </div>
        `;
    } catch (e) {
        c.innerHTML = `<div class="alert alert-danger">Error: ${e.message}</div>`;
    }
}
