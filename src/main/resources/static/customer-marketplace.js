let marketplaceProducts = [];
let marketplaceDisplayedCount = 0;
let marketplaceDebounceTimer = null;

const MARKETPLACE_PAGE_SIZE = 12;

function getMarketplaceUser() {
    return AgriAuth.getUser();
}

function initMarketplace() {
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const categorySelect = document.getElementById('categorySelect');
    const loadMoreBtn = document.getElementById('loadMoreBtn');

    if (!searchInput || !searchBtn || !categorySelect || !loadMoreBtn) {
        console.error('Marketplace elements are missing.');
        return;
    }

    searchInput.addEventListener('keyup', () => {
        clearTimeout(marketplaceDebounceTimer);

        marketplaceDebounceTimer = setTimeout(() => {
            loadMarketplaceProducts();
        }, 300);
    });

    searchBtn.addEventListener('click', () => {
        loadMarketplaceProducts();
    });

    categorySelect.addEventListener('change', () => {
        loadMarketplaceProducts();
    });

    loadMoreBtn.addEventListener('click', () => {
        renderNextMarketplaceBatch();
    });

    loadMarketplaceCategories();
    loadMarketplaceProducts();
}

async function loadMarketplaceCategories() {
    const categorySelect = document.getElementById('categorySelect');

    try {
        const response = await FarmerAPI.get('/api/products');

        if (!response.ok || !Array.isArray(response.data)) {
            return;
        }

        const categories = new Set();

        response.data.forEach(product => {
            if (product.category) {
                categories.add(product.category);
            }
        });

        categorySelect.innerHTML = '<option value="">All Categories</option>';

        Array.from(categories)
            .sort()
            .forEach(category => {
                const option = document.createElement('option');
                option.value = category;
                option.textContent = category;
                categorySelect.appendChild(option);
            });

    } catch (error) {
        console.error('Failed to load marketplace categories:', error);
    }
}

async function loadMarketplaceProducts() {
    const searchInput = document.getElementById('searchInput');
    const categorySelect = document.getElementById('categorySelect');

    const query = searchInput.value.trim();
    const category = categorySelect.value;

    const params = new URLSearchParams();

    if (query) {
        params.append('query', query);
    }

    if (category) {
        params.append('category', category);
    }

    const url = params.toString()
        ? `/api/products?${params.toString()}`
        : '/api/products';

    showMarketplaceLoading(true);
    hideMarketplaceError();

    try {
        const response = await FarmerAPI.get(url);

        if (!response.ok) {
            throw new Error(
                response.data?.error || 'Failed to load products'
            );
        }

        marketplaceProducts = Array.isArray(response.data)
            ? response.data
            : [];

        marketplaceDisplayedCount = 0;

        const grid = document.getElementById('productGrid');

        if (grid) {
            grid.innerHTML = '';
        }

        renderNextMarketplaceBatch();

    } catch (error) {
        console.error('Failed to load marketplace products:', error);
        showMarketplaceError(error.message);
    } finally {
        showMarketplaceLoading(false);
    }
}

function renderNextMarketplaceBatch() {
    const grid = document.getElementById('productGrid');
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    const emptyProducts = document.getElementById('emptyProducts');

    if (!grid) {
        return;
    }

    const productsToRender = marketplaceProducts.slice(
        marketplaceDisplayedCount,
        marketplaceDisplayedCount + MARKETPLACE_PAGE_SIZE
    );

    productsToRender.forEach(product => {
        const card = document.createElement('div');

        card.className = 'col-sm-6 col-lg-4 col-xl-3';

      card.innerHTML = `
    <div class="card h-100 shadow-sm border-0">

        <div class="card-body d-flex flex-column">

            <span class="badge bg-success-subtle text-success align-self-start mb-2">
                ${escapeMarketplaceHtml(product.category || 'Produce')}
            </span>

            <h5 class="card-title fw-bold">
                ${escapeMarketplaceHtml(product.name)}
            </h5>

            <p class="card-text text-muted small">
                ${escapeMarketplaceHtml(
                    product.description || 'Fresh farm produce'
                )}
            </p>

            <div class="mt-auto">

                <div class="mb-2">
                    <span class="fs-5 fw-bold text-success">
                        ₹${Number(product.price || 0).toFixed(2)}
                    </span>
                    <span class="text-muted">
                        / ${escapeMarketplaceHtml(product.unit || '')}
                    </span>
                </div>

                <div class="small text-muted mb-3">
                    <i class="bi bi-person"></i>
                    ${escapeMarketplaceHtml(
                        product.farmerName || 'Farmer'
                    )}
                    <br>
                    <i class="bi bi-geo-alt"></i>
                    ${escapeMarketplaceHtml(
                        product.farmerLocation ||
                        product.farmerAddress ||
                        'Location unavailable'
                    )}
                </div>

                <div class="d-flex gap-2">

                    <button
                        class="btn btn-outline-success btn-sm flex-grow-1"
                        onclick="showMarketplaceProductDetails(${product.id})"
                    >
                        <i class="bi bi-eye"></i>
                        Details
                    </button>

                    <button
                        class="btn btn-success btn-sm flex-grow-1"
                        onclick="addMarketplaceToCart(${product.id})"
                    >
                        <i class="bi bi-cart-plus"></i>
                        Add
                    </button>

                </div>

            </div>

        </div>

    </div>
`;

        grid.appendChild(card);
    });

    marketplaceDisplayedCount += productsToRender.length;

    if (emptyProducts) {
        emptyProducts.classList.toggle(
            'd-none',
            marketplaceProducts.length !== 0
        );
    }

    if (loadMoreBtn) {
        loadMoreBtn.classList.toggle(
            'd-none',
            marketplaceDisplayedCount >= marketplaceProducts.length
        );
    }
}

async function showMarketplaceProductDetails(productId) {
    try {
        const response = await FarmerAPI.get(
            `/api/products/${productId}`
        );

        if (!response.ok) {
            throw new Error('Failed to load product details');
        }

        const product = response.data;

        let ratingHtml = `
            <span class="text-muted small">
                No reviews yet
            </span>
        `;

        try {
            const reviewResponse = await FarmerAPI.get(
                `/api/reviews/product/${productId}/summary`
            );

            if (reviewResponse.ok && reviewResponse.data) {
                const average =
                    Number(reviewResponse.data.averageRating || 0);

                const count =
                    reviewResponse.data.totalReviews ||
                    reviewResponse.data.totalReviewCount ||
                    0;

                ratingHtml = `
                    <div class="mb-3">
                        <span class="badge bg-warning text-dark">
                            <i class="bi bi-star-fill"></i>
                            ${average.toFixed(1)}
                        </span>

                        <span class="text-muted small ms-2">
                            ${count} review${count === 1 ? '' : 's'}
                        </span>
                    </div>
                `;
            }
        } catch (error) {
            console.warn('Review summary unavailable:', error);
        }

        const modalBody =
            document.getElementById('productDetailBody');

        modalBody.innerHTML = `
            <div class="row g-4">

                <div class="col-md-5">

                    <img
                        src="${escapeMarketplaceHtml(
                            product.imageUrl ||
                            ''
                        )}"
                        class="img-fluid rounded"
                        alt="${escapeMarketplaceHtml(product.name)}"
                    >

                </div>

                <div class="col-md-7">

                    <h4 class="fw-bold">
                        ${escapeMarketplaceHtml(product.name)}
                    </h4>

                    ${ratingHtml}

                    <p class="text-muted">
                        ${escapeMarketplaceHtml(
                            product.description || 'Fresh farm produce'
                        )}
                    </p>

                    <p>
                        <strong>Category:</strong>
                        ${escapeMarketplaceHtml(product.category || '')}
                    </p>

                    <p>
                        <strong>Price:</strong>
                        ₹${Number(product.price || 0).toFixed(2)}
                        / ${escapeMarketplaceHtml(product.unit || '')}
                    </p>

                    <p>
                        <strong>Available Quantity:</strong>
                        ${product.quantity || 0}
                        ${escapeMarketplaceHtml(product.unit || '')}
                    </p>

                    <p>
                        <strong>Farmer:</strong>
                        ${escapeMarketplaceHtml(
                            product.farmerName || 'Farmer'
                        )}
                    </p>

                    <p>
                        <strong>Location:</strong>
                        ${escapeMarketplaceHtml(
                            product.farmerLocation ||
                            product.farmerAddress ||
                            'Location unavailable'
                        )}
                    </p>

                    <button
                        class="btn btn-success"
                        onclick="addMarketplaceToCart(${product.id})"
                        data-bs-dismiss="modal"
                    >
                        <i class="bi bi-cart-plus"></i>
                        Add to Cart
                    </button>

                </div>

            </div>
        `;

        const modalElement =
            document.getElementById('productDetailModal');

        const modal =
            bootstrap.Modal.getOrCreateInstance(modalElement);

        modal.show();

    } catch (error) {
        console.error('Product details error:', error);
        showMarketplaceError(error.message);
    }
}

async function addMarketplaceToCart(productId) {
    const user = getMarketplaceUser();

    if (!user) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch(
            `/api/cart/${user.id}/items`,
            {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    productId: productId,
                    quantity: 1
                })
            }
        );

        const data = await response.json();

        if (!response.ok) {
            throw new Error(
                data.error || 'Failed to add product to cart'
            );
        }

        showMarketplaceMessage(
            'Produce added to your cart successfully.'
        );

        updateMarketplaceCartBadge();

    } catch (error) {
        console.error('Add to cart error:', error);

        showMarketplaceError(
            error.message || 'Failed to add product to cart'
        );
    }
}

async function updateMarketplaceCartBadge() {
    const user = getMarketplaceUser();

    if (!user) {
        return;
    }

    const badge =
        document.getElementById('cartCountBadge');

    if (!badge) {
        return;
    }

    try {
        const response = await FarmerAPI.get(
            `/api/cart/${user.id}`
        );

        if (!response.ok) {
            return;
        }

        const data = response.data;

        if (data && typeof data.totalItems === 'number') {
            badge.textContent = data.totalItems;
        } else if (Array.isArray(data)) {
            badge.textContent = data.reduce(
                (total, item) => total + Number(item.quantity || 0),
                0
            );
        }

    } catch (error) {
        console.warn(
            'Unable to update cart badge:',
            error
        );
    }
}

function showMarketplaceLoading(show) {
    const loading =
        document.getElementById('marketplaceLoading');

    if (loading) {
        loading.classList.toggle('d-none', !show);
    }
}

function showMarketplaceError(message) {
    const errorBox =
        document.getElementById('marketplaceError');

    if (!errorBox) {
        return;
    }

    errorBox.textContent =
        message || 'Unable to load marketplace.';

    errorBox.classList.remove('d-none');
}

function hideMarketplaceError() {
    const errorBox =
        document.getElementById('marketplaceError');

    if (errorBox) {
        errorBox.classList.add('d-none');
    }
}

function showMarketplaceMessage(message) {
    const existing =
        document.getElementById('marketplaceMessage');

    if (existing) {
        existing.remove();
    }

    const alert = document.createElement('div');

    alert.id = 'marketplaceMessage';

    alert.className =
        'alert alert-success position-fixed top-0 end-0 m-3 shadow';

    alert.style.zIndex = '9999';

    alert.innerHTML = `
        <i class="bi bi-check-circle"></i>
        ${escapeMarketplaceHtml(message)}
    `;

    document.body.appendChild(alert);

    setTimeout(() => {
        alert.remove();
    }, 2500);
}

function escapeMarketplaceHtml(value) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}