// ===============================
// Configuration
// ===============================
const SERVER_PORT = 8181; // server.port
const API_PREFIX = '/chronos'; // api.prefix
const BASE_URL = `http://localhost:${SERVER_PORT}${API_PREFIX}/api/retry-policies`;

// ===============================
// DOM Elements
// ===============================
const createPolicyForm = document.getElementById('createPolicyForm');
const policiesTableBody = document.getElementById('policiesTableBody');

// Modal Elements for Update
const modal = document.getElementById('updateModal');
const modalForm = document.getElementById('updatePolicyForm');

// ===============================
// Helper Functions
// ===============================
async function fetchPolicies() {
    try {
        const response = await fetch(BASE_URL);
        const data = await response.json();
        if (data.success) renderPolicies(data.data);
        else console.error('Failed to fetch policies', data);
    } catch (error) {
        console.error('Error fetching policies:', error);
    }
}

function renderPolicies(policies) {
    policiesTableBody.innerHTML = '';

    if (!policies || policies.length === 0) {
        policiesTableBody.innerHTML = '<tr><td colspan="7">No policies found</td></tr>';
        return;
    }

    policies.forEach(policy => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${policy.id}</td>
            <td>${policy.strategy}</td>
            <td>${policy.backoffInitialMs}</td>
            <td>${policy.backoffFactor}</td>
            <td>${policy.maxAttempts}</td>
            <td>${policy.jitterMs}</td>
            <td>
                <button class="edit-btn" data-id="${policy.id}">Edit</button>
                <button class="delete-btn" data-id="${policy.id}">Delete</button>
            </td>
        `;
        policiesTableBody.appendChild(row);
    });

    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', () => deletePolicy(btn.dataset.id));
    });
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', () => openUpdateModal(btn.dataset.id));
    });
}

// ===============================
// Create Policy
// ===============================
async function createPolicy(event) {
    event.preventDefault();
    const submitBtn = createPolicyForm.querySelector('button[type="submit"]');
    submitBtn.disabled = true;

    const formData = new FormData(createPolicyForm);
    const payload = {
        strategy: formData.get('strategy'),
        backoffInitialMs: parseInt(formData.get('backoffInitialMs')) || 0,
        backoffFactor: parseFloat(formData.get('backoffFactor')) || 1,
        maxAttempts: parseInt(formData.get('maxAttempts')) || 1,
        jitterMs: parseInt(formData.get('jitterMs')) || 0
    };

    if (payload.maxAttempts < 1) {
        alert('Max Attempts must be at least 1');
        submitBtn.disabled = false;
        return;
    }

    try {
        const res = await fetch(`${BASE_URL}/create`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (data.success) {
            createPolicyForm.reset();
            fetchPolicies();
        } else alert('Error creating policy: ' + (data.message || 'Unknown error'));
    } catch (error) {
        console.error('Error creating policy:', error);
    } finally {
        submitBtn.disabled = false;
    }
}

// ===============================
// Delete Policy
// ===============================
async function deletePolicy(policyId) {
    if (!confirm('Are you sure you want to delete this policy?')) return;
    try {
        const res = await fetch(`${BASE_URL}/${policyId}`, { method: 'DELETE' });
        const data = await res.json();
        if (data.success) fetchPolicies();
        else alert('Error deleting policy: ' + (data.message || 'Unknown error'));
    } catch (error) {
        console.error('Error deleting policy:', error);
    }
}

// ===============================
// Update Policy
// ===============================
async function openUpdateModal(policyId) {
    try {
        const res = await fetch(`${BASE_URL}/${policyId}`);
        const data = await res.json();
        if (data.success) {
            const p = data.data;
            modalForm.id.value = p.id;
            modalForm.strategy.value = p.strategy;
            modalForm.backoffInitialMs.value = p.backoffInitialMs;
            modalForm.backoffFactor.value = p.backoffFactor;
            modalForm.maxAttempts.value = p.maxAttempts;
            modalForm.jitterMs.value = p.jitterMs;
            modal.style.display = 'flex';
        }
    } catch (error) {
        console.error('Error fetching policy for update:', error);
    }
}

async function submitUpdatePolicy(event) {
    event.preventDefault();
    const submitBtn = modalForm.querySelector('button[type="submit"]');
    submitBtn.disabled = true;

    const formData = new FormData(modalForm);
    const policyId = formData.get('id');
    const payload = {
        strategy: formData.get('strategy'),
        backoffInitialMs: parseInt(formData.get('backoffInitialMs')) || 0,
        backoffFactor: parseFloat(formData.get('backoffFactor')) || 1,
        maxAttempts: parseInt(formData.get('maxAttempts')) || 1,
        jitterMs: parseInt(formData.get('jitterMs')) || 0
    };

    if (payload.maxAttempts < 1) {
        alert('Max Attempts must be at least 1');
        submitBtn.disabled = false;
        return;
    }

    try {
        const res = await fetch(`${BASE_URL}/${policyId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (data.success) {
            modal.style.display = 'none';
            fetchPolicies();
        } else alert('Error updating policy: ' + (data.message || 'Unknown error'));
    } catch (error) {
        console.error('Error updating policy:', error);
    } finally {
        submitBtn.disabled = false;
    }
}

// ===============================
// Close Modal on Outside Click
// ===============================
window.addEventListener('click', (e) => {
    if (e.target === modal) modal.style.display = 'none';
});

// ===============================
// Event Listeners
// ===============================
createPolicyForm.addEventListener('submit', createPolicy);
modalForm.addEventListener('submit', submitUpdatePolicy);

// ===============================
// Initial Load
// ===============================
fetchPolicies();
