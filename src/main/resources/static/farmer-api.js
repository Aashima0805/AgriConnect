// Farmer module shared API helper and UI utilities
const FarmerAPI = {
  baseUrl: 'http://localhost:8081',

  // Get logged‑in farmer id from session
  getFarmerId() {
    const user = AgriAuth.getUser();
    return user && user.role === 'FARMER' ? user.id : null;
  },

  // Generic fetch wrappers (no Bearer token – session cookie is used)
  async get(path) {
    const resp = await fetch(this.baseUrl + path);
    return resp.json().then(data => ({ok: resp.ok, data}));
  },
  async post(path, body) {
    const resp = await fetch(this.baseUrl + path, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(body)
    });
    return resp.json().then(data => ({ok: resp.ok, data}));
  },
  async put(path, body) {
    const resp = await fetch(this.baseUrl + path, {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(body)
    });
    return resp.json().then(data => ({ok: resp.ok, data}));
  },
  async del(path) {
    const resp = await fetch(this.baseUrl + path, {method: 'DELETE'});
    return {ok: resp.ok};
  },

  // UI helpers
  showLoading(show) {
    const el = document.getElementById('loadingOverlay');
    if (el) el.style.display = show ? 'flex' : 'none';
  },
  showAlert(type, msg) {
    const container = document.getElementById('alertContainer');
    if (!container) return;
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = msg;
    container.innerHTML = '';
    container.appendChild(alertDiv);
    setTimeout(() => container.removeChild(alertDiv), 5000);
  },
  confirmDelete(callback) {
    if (confirm('Are you sure you want to delete this item?')) callback();
  }
};

// Export to global scope for page scripts
window.FarmerAPI = FarmerAPI;
