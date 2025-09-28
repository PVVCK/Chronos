document.addEventListener('DOMContentLoaded', () => {
    const BASE_URL = "http://localhost:8181/chronos/api/jobs";
    const jobsTable = document.getElementById('jobsTableBody');
    const createJobForm = document.getElementById('createJobForm');

    // Replace with dynamic tenantId if needed
    const TENANT_ID = 'tenant1';

    // Backend enum values for Job.type
    const JOB_TYPE_ENUM = ['HTTP', 'SHELL', 'SCRIPT'];

    function createDropdown(enumArray, name) {
        const select = document.createElement('select');
        select.name = name;
        select.required = true;
        enumArray.forEach(v => {
            const opt = document.createElement('option');
            opt.value = v;
            opt.textContent = v;
            select.appendChild(opt);
        });
        return select;
    }

    // Insert dropdown for Job.type
    document.getElementById('jobTypeContainer').appendChild(createDropdown(JOB_TYPE_ENUM, 'type'));

    // ------------------ Fetch Jobs ------------------
    async function fetchJobs() {
        try {
            const response = await fetch(`${BASE_URL}/tenant/${TENANT_ID}`);
            const data = await response.json();

            if (data.success && data.data.length > 0) {
                renderJobs(data.data);
            } else {
                jobsTable.innerHTML = '<tr><td colspan="9">No jobs found</td></tr>';
            }
        } catch (err) {
            console.error('Error fetching jobs:', err);
            alert('Failed to fetch jobs. Check console for details.');
        }
    }

    // ------------------ Render Jobs ------------------
    function renderJobs(jobs) {
        jobsTable.innerHTML = '';
        jobs.forEach(job => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${job.id}</td>
                <td>${job.name}</td>
                <td>${job.type}</td>
                <td>${job.payload}</td>
                <td>${job.timeout ?? '-'}</td>
                <td>${job.maxAttempts}</td>
                <td>${job.retryPolicyId ?? '-'}</td>
                <td>${job.ownerUserId ?? '-'}</td>
                <td>
                    <button class="edit-btn" data-id="${job.id}">Edit</button>
                    <button class="delete-btn" data-id="${job.id}">Delete</button>
                </td>
            `;
            jobsTable.appendChild(tr);
        });

        // Attach event listeners
        document.querySelectorAll('.edit-btn').forEach(btn => btn.addEventListener('click', () => editJob(btn.dataset.id)));
        document.querySelectorAll('.delete-btn').forEach(btn => btn.addEventListener('click', () => deleteJob(btn.dataset.id)));
    }

    // ------------------ Create Job ------------------
    createJobForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(createJobForm);
        const jobData = {
            tenantId: TENANT_ID,
            name: formData.get('name'),
            type: formData.get('type'),
            payload: formData.get('payload'),
            timeout: `PT${parseInt(formData.get('timeout'))}S`, // convert to ISO-8601
            maxAttempts: parseInt(formData.get('maxAttempts')),
            retryPolicyId: formData.get('retryPolicyId') || null,
            ownerUserId: formData.get('ownerUserId') || null
        };

        try {
            const response = await fetch(`${BASE_URL}/create`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(jobData)
            });
            const data = await response.json();
            if (data.success) {
                fetchJobs();
                createJobForm.reset();
            } else {
                alert(data.message || 'Failed to create job.');
            }
        } catch (err) {
            console.error('Error creating job:', err);
            alert('Error creating job. Check console for details.');
        }
    });

    // ------------------ Edit Job ------------------
    async function editJob(jobId) {
        let job;
        try {
            const response = await fetch(`${BASE_URL}/${jobId}`);
            const data = await response.json();
            if (!data.success) return alert('Failed to fetch job details.');
            job = data.data;
        } catch (err) {
            console.error('Error fetching job:', err);
            return alert('Error fetching job. Check console for details.');
        }

        const newName = prompt('Enter job name:', job.name);
        const newType = prompt('Enter job type (HTTP/SHELL/SCRIPT):', job.type);
        const newPayload = prompt('Enter job payload:', job.payload);
        const newTimeout = prompt('Enter job timeout (seconds):', job.timeout ? parseInt(job.timeout.replace(/[^\d]/g, '')) : 30);
        const newMaxAttempts = prompt('Enter max attempts:', job.maxAttempts);
        const newRetryPolicyId = prompt('Enter retry policy ID:', job.retryPolicyId ?? '');
        const newOwnerUserId = prompt('Enter owner user ID:', job.ownerUserId ?? '');

        const updatedJob = {
            name: newName,
            type: newType,
            payload: newPayload,
            timeout: `PT${parseInt(newTimeout)}S`, // convert to ISO-8601
            maxAttempts: parseInt(newMaxAttempts),
            retryPolicyId: newRetryPolicyId || null,
            ownerUserId: newOwnerUserId || null
        };

        try {
            const response = await fetch(`${BASE_URL}/${jobId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updatedJob)
            });
            const data = await response.json();
            if (data.success) fetchJobs();
            else alert('Failed to update job.');
        } catch (err) {
            console.error('Error updating job:', err);
            alert('Error updating job. Check console for details.');
        }
    }

    // ------------------ Delete Job ------------------
    async function deleteJob(jobId) {
        if (!confirm('Are you sure you want to delete this job?')) return;

        try {
            await fetch(`${BASE_URL}/${jobId}`, { method: 'DELETE' });
            fetchJobs();
        } catch (err) {
            console.error('Error deleting job:', err);
            alert('Error deleting job.');
        }
    }

    // ------------------ Initial Fetch ------------------
    fetchJobs();
});
