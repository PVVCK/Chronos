// Base URL for Execution API
const BASE_URL = "http://localhost:8181/chronos/api/executions";

// -------------------- Helpers --------------------
function openModal(modalId) {
    document.getElementById(modalId).style.display = 'flex';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// Converts datetime-local input to ISO string
function formatDateTimeLocal(input) {
    if (!input) return null;
    return new Date(input).toISOString();
}

// -------------------- Fetch All Executions --------------------
async function fetchExecutions() {
    try {
        const response = await fetch(BASE_URL); // fetch all executions
        const data = await response.json();

        const tableBody = document.getElementById("executionsTableBody");
        tableBody.innerHTML = "";

        if (data?.data?.length > 0) {
            data.data.forEach(exec => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${exec.id || "-"}</td>
                    <td>${exec.scheduleId || "-"}</td>
                    <td>${exec.jobId || "-"}</td>
                    <td>${exec.status || "-"}</td>
                    <td>${exec.dueAt || "-"}</td>
                    <td>${exec.enqueuedAt || "-"}</td>
                    <td>${exec.startedAt || "-"}</td>
                    <td>${exec.finishedAt || "-"}</td>
                    <td>${exec.attemptCount || 0}</td>
                    <td>${exec.correlationId || "-"}</td>
                    <td>
                        <button onclick="openStartModal('${exec.id}')">Start</button>
                        <button onclick="openFinishModal('${exec.id}')">Finish</button>
                        <button onclick="openCancelModal('${exec.id}')">Cancel</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        } else {
            tableBody.innerHTML = `<tr><td colspan="11">No executions found</td></tr>`;
        }
    } catch (err) {
        console.error("Error fetching executions:", err);
    }
}

// -------------------- Create Execution --------------------
document.getElementById("createExecutionForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const scheduleId = e.target.scheduleId.value;
    const dueAt = formatDateTimeLocal(e.target.dueAt.value);

    try {
        const response = await fetch(`${BASE_URL}/create`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ scheduleId, dueAt })
        });
        const data = await response.json();
        alert(JSON.stringify(data, null, 2));
        e.target.reset();
        fetchExecutions();
    } catch (err) {
        console.error("Error creating execution:", err);
    }
});

// -------------------- Fetch Execution by ID --------------------
document.getElementById("fetchExecutionForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = e.target.executionId.value;
    try {
        const response = await fetch(`${BASE_URL}/${id}`);
        const data = await response.json();
        document.getElementById("executionDetails").textContent = JSON.stringify(data, null, 2);
    } catch (err) {
        console.error("Error fetching execution:", err);
    }
});

// -------------------- Get Due Executions --------------------
document.getElementById("dueExecutionsForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const cutoffTime = formatDateTimeLocal(e.target.cutoffTime.value);
    try {
        const response = await fetch(`${BASE_URL}/due?cutoffTime=${encodeURIComponent(cutoffTime)}`);
        const data = await response.json();
        document.getElementById("dueExecutionsList").textContent = JSON.stringify(data, null, 2);
    } catch (err) {
        console.error("Error fetching due executions:", err);
    }
});

// -------------------- Start Execution --------------------
function openStartModal(id) {
    document.getElementById("startExecutionId").value = id;
    openModal("startExecutionModal");
}

document.getElementById("startExecutionForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("startExecutionId").value;
    const startedAt = formatDateTimeLocal(document.getElementById("startStartedAt").value);
    let url = `${BASE_URL}/${id}/start`;
    if (startedAt) url += `?startedAt=${encodeURIComponent(startedAt)}`;

    try {
        const response = await fetch(url, { method: "POST" });
        const data = await response.json();
        alert(JSON.stringify(data, null, 2));
        closeModal("startExecutionModal");
        fetchExecutions();
    } catch (err) {
        console.error("Error starting execution:", err);
    }
});

// -------------------- Finish Execution --------------------
function openFinishModal(id) {
    document.getElementById("finishExecutionId").value = id;
    openModal("finishExecutionModal");
}

document.getElementById("finishExecutionForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("finishExecutionId").value;
    const success = document.getElementById("finishSuccess").value;
    const finishedAt = formatDateTimeLocal(document.getElementById("finishFinishedAt").value);

    let url = `${BASE_URL}/${id}/finish?success=${success}`;
    if (finishedAt) url += `&finishedAt=${encodeURIComponent(finishedAt)}`;

    try {
        const response = await fetch(url, { method: "POST" });
        const data = await response.json();
        alert(JSON.stringify(data, null, 2));
        closeModal("finishExecutionModal");
        fetchExecutions();
    } catch (err) {
        console.error("Error finishing execution:", err);
    }
});

// -------------------- Cancel Execution --------------------
function openCancelModal(id) {
    document.getElementById("cancelExecutionId").value = id;
    openModal("cancelExecutionModal");
}

document.getElementById("cancelExecutionForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("cancelExecutionId").value;
    const reason = document.getElementById("cancelReason").value;

    try {
        const response = await fetch(`${BASE_URL}/${id}/cancel`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ reason })
        });
        const data = await response.json();
        alert(JSON.stringify(data, null, 2));
        closeModal("cancelExecutionModal");
        fetchExecutions();
    } catch (err) {
        console.error("Error cancelling execution:", err);
    }
});

// -------------------- Initial Load --------------------
document.addEventListener("DOMContentLoaded", fetchExecutions);
