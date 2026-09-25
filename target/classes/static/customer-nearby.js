let nearbyProducts = [];
let nearbyDisplayedCount = 0;
const nearbyBatchSize = 6;
let nearbyUser = null;

async function initNearbyProducts(user) {
nearbyUser = user;


const searchInput = document.getElementById('searchInput');
const searchBtn = document.getElementById('searchBtn');
const radiusSelect = document.getElementById('radiusSelect');
const loadMoreBtn = document.getElementById('loadMoreBtn');

if (searchBtn) {
    searchBtn.addEventListener('click', loadNearbyProducts);
}

if (searchInput) {
    searchInput.addEventListener('keydown', event => {
        if (event.key === 'Enter') {
            loadNearbyProducts();
        }
    });
}

if (radiusSelect) {
    radiusSelect.addEventListener('change', loadNearbyProducts);
}

if (loadMoreBtn) {
    loadMoreBtn.addEventListener('click', renderNextNearbyBatch);
}

await loadNearbyProducts();


}

async function loadNearbyProducts() {
const radius = document.getElementById('radiusSelect').value;
const searchText = document.getElementById('searchInput').value.trim().toLowerCase();


showNearbyLoading();
hideNearbyError();
hideNearbyMessage();

try {
    const response = await fetch(
        `/api/products/nearby?customerId=${nearbyUser.id}&radiusKm=${radius}`
    );

    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || 'Unable to load nearby products.');
    }

    const products = await response.json();

    nearbyProducts = products.filter(product => {
        if (!searchText) {
            return true;
        }

        const name = product.name || '';
        const category = product.category || '';
        const description = product.description || '';
        const farmerName = product.farmerName || '';

        return (
            name.toLowerCase().includes(searchText) ||
            category.toLowerCase().includes(searchText) ||
            description.toLowerCase().includes(searchText) ||
            farmerName.toLowerCase().includes(searchText)
        );
    });

    nearbyDisplayedCount = 0;

    renderNearbyProducts();

    if (nearbyProducts.length === 0) {
        showNearbyMessage(
            'No products were found within the selected distance.'
        );
    }
} catch (error) {
    console.error(error);

    showNearbyError(
        'Unable to load nearby products. Please check your location details and try again.'
    );
} finally {
    hideNearbyLoading();
}

}

function renderNearbyProducts() {
const productGrid = document.getElementById('productGrid');


if (!productGrid) {
    return;
}

if (nearbyDisplayedCount === 0) {
    productGrid.innerHTML = '';
}

const nextProducts = nearbyProducts.slice(
    nearbyDisplayedCount,
    nearbyDisplayedCount + nearbyBatchSize
);

nextProducts.forEach(product => {
    productGrid.insertAdjacentHTML(
        'beforeend',
        createNearbyProductCard(product)
    );
});

nearbyDisplayedCount += nextProducts.length;

const loadMoreBtn = document.getElementById('loadMoreBtn');

if (loadMoreBtn) {
    if (nearbyDisplayedCount < nearbyProducts.length) {
        loadMoreBtn.classList.remove('d-none');
    } else {
        loadMoreBtn.classList.add('d-none');
    }
}


}

function renderNextNearbyBatch() {
renderNearbyProducts();
}

function createNearbyProductCard(product) {
    const price = product.price != null
        ? `₹${Number(product.price).toFixed(2)}`
        : 'Price unavailable';

    const quantity = product.quantity != null
        ? `${product.quantity} ${product.unit || ''}`
        : 'Quantity unavailable';

    const distance = product.distanceKm != null
        ? `${Number(product.distanceKm).toFixed(1)} km away`
        : 'Distance unavailable';

    return `
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-0">

                <div class="card-body d-flex flex-column">

                    <span class="badge bg-success-subtle text-success mb-2 align-self-start">
                        ${escapeNearbyHtml(product.category || 'Produce')}
                    </span>

                    <h5 class="card-title fw-bold">
                        ${escapeNearbyHtml(product.name || 'Unnamed Product')}
                    </h5>

                    <p class="card-text text-muted small">
                        ${escapeNearbyHtml(
                            product.description ||
                            'Fresh agricultural produce.'
                        )}
                    </p>

                    <div class="mb-2">
                        <strong class="text-success fs-5">${price}</strong>
                        <span class="text-muted">
                            / ${escapeNearbyHtml(product.unit || 'unit')}
                        </span>
                    </div>

                    <div class="small text-muted mb-2">
                        Available: ${escapeNearbyHtml(String(quantity))}
                    </div>

                    <div class="small text-muted mb-3">
                        Farmer: ${escapeNearbyHtml(product.farmerName || 'Farmer')}
                    </div>

                    <div class="small text-primary mb-3">
                        ${escapeNearbyHtml(distance)}
                    </div>

                    <div class="mt-auto d-flex gap-2">
                        <button
                            class="btn btn-outline-success flex-fill"
                            onclick="showNearbyProductDetails(${product.id})">
                            View
                        </button>

                        <button
                            class="btn btn-success flex-fill"
                            onclick="addNearbyToCart(${product.id})">
                            Add to Cart
                        </button>
                    </div>

                </div>

            </div>
        </div>
    `;
}

function showNearbyProductDetails(productId) {
const product = nearbyProducts.find(item => item.id === productId);

if (!product) {
    return;
}

const body = document.getElementById('productDetailBody');
const title = document.getElementById('productDetailLabel');

if (!body || !title) {
    return;
}

title.textContent = product.name || 'Product Details';

const price = product.price != null
    ? `₹${Number(product.price).toFixed(2)}`
    : 'Price unavailable';

const distance = product.distanceKm != null
    ? `${Number(product.distanceKm).toFixed(1)} km`
    : 'Distance unavailable';

body.innerHTML = `
    <div class="row g-4">

        <div class="col-md-5">
            ${
                product.imageUrl
                    ? `
                        <img
                            src="${escapeNearbyHtml(product.imageUrl)}"
                            class="img-fluid rounded"
                            alt="${escapeNearbyHtml(product.name || 'Product')}"
                        >
                    `
                    : `
                        <div
                            class="bg-light rounded d-flex align-items-center justify-content-center"
                            style="height:250px;"
                        >
                            <div class="text-center text-success">
                                <div style="font-size:60px;">🌾</div>
                                <div>Farm Produce</div>
                            </div>
                        </div>
                    `
            }
        </div>

        <div class="col-md-7">

            <h4 class="fw-bold">
                ${escapeNearbyHtml(product.name || 'Unnamed Product')}
            </h4>

            <p class="text-muted">
                ${escapeNearbyHtml(product.description || 'No description available.')}
            </p>

            <p>
                <strong>Category:</strong>
                ${escapeNearbyHtml(product.category || 'Not specified')}
            </p>

            <p>
                <strong>Price:</strong>
                ₹${product.price != null ? Number(product.price).toFixed(2) : 'N/A'}
            </p>

            <p>
                <strong>Available:</strong>
                ${escapeNearbyHtml(String(product.quantity ?? 'N/A'))}
                ${escapeNearbyHtml(product.unit || '')}
            </p>

            <p>
                <strong>Farmer:</strong>
                ${escapeNearbyHtml(product.farmerName || 'Farmer')}
            </p>

            <p>
                <strong>Location:</strong>
                ${escapeNearbyHtml(
                    product.farmerLocation ||
                    product.farmerAddress ||
                    'Location not available'
                )}
            </p>

            <p>
                <strong>Distance:</strong>
                ${escapeNearbyHtml(distance)}
            </p>

            <button
                class="btn btn-success"
                onclick="addNearbyToCart(${product.id})">
                Add to Cart
            </button>

        </div>
    </div>
`;

const modalElement = document.getElementById('productDetailModal');

if (modalElement) {
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
    modal.show();
}


}

async function addNearbyToCart(productId) {
if (!nearbyUser) {
return;
}


try {
    const response = await fetch(`/api/cart/${nearbyUser.id}/items`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
            productId: productId,
            quantity: 1
        })
    });

    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || 'Unable to add product to cart.');
    }

    alert('Product added to cart.');

    const modalElement = document.getElementById('productDetailModal');

    if (modalElement) {
        const modal = bootstrap.Modal.getInstance(modalElement);

        if (modal) {
            modal.hide();
        }
    }
} catch (error) {
    console.error(error);
    alert('Unable to add this product to cart.');
}


}

function showNearbyLoading() {
const loading = document.getElementById('nearbyLoading');


if (loading) {
    loading.classList.remove('d-none');
}


}

function hideNearbyLoading() {
const loading = document.getElementById('nearbyLoading');


if (loading) {
    loading.classList.add('d-none');
}


}

function showNearbyError(message) {
const error = document.getElementById('nearbyError');


if (error) {
    error.textContent = message;
    error.classList.remove('d-none');
}


}

function hideNearbyError() {
const error = document.getElementById('nearbyError');


if (error) {
    error.classList.add('d-none');
}


}

function showNearbyMessage(message) {
const messageBox = document.getElementById('nearbyMessage');


if (messageBox) {
    messageBox.textContent = message;
    messageBox.classList.remove('d-none');
}

}

function hideNearbyMessage() {
const messageBox = document.getElementById('nearbyMessage');


if (messageBox) {
    messageBox.classList.add('d-none');
}

}

function escapeNearbyHtml(value) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
