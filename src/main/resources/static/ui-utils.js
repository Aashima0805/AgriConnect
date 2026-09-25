// UI utility helpers for loading overlay and alerts
const UIUtils = {
  showLoading(show) {
    const el = document.getElementById('loadingOverlay');
    if (el) el.style.display = show ? 'flex' : 'none';
  },
  showAlert(type, message) {
    const container = document.getElementById('alertContainer');
    if (!container) return;
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `${message}<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;
    container.innerHTML = '';
    container.appendChild(alertDiv);
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
      const bsAlert = bootstrap.Alert.getInstance(alertDiv);
      if (bsAlert) bsAlert.close();
    }, 5000);
  }
};

window.UIUtils = UIUtils;
