document.addEventListener('DOMContentLoaded', () => {
    const BASE_URL = "http://localhost:8181/chronos/api/schedules";
    const schedulesTable = document.getElementById('schedulesTableBody');
    const createScheduleForm = document.getElementById('createScheduleForm');
    const jobIdInput = createScheduleForm.querySelector('input[name="jobId"]');

    const editModal = document.getElementById('editModal');
    const editScheduleForm = document.getElementById('editScheduleForm');

    const SCHEDULE_TYPE_ENUM = ['CRON', 'FIXED_RATE', 'FIXED_DELAY'];
    const MISFIRE_ENUM = ['FIRE_IMMEDIATELY', 'SKIP', 'RESCHEDULE_NEXT'];

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

    document.getElementById('scheduleTypeContainer').appendChild(createDropdown(SCHEDULE_TYPE_ENUM, 'scheduleType'));
    document.getElementById('misfirePolicyContainer').appendChild(createDropdown(MISFIRE_ENUM, 'misfirePolicy'));
    document.getElementById('editScheduleTypeContainer').appendChild(createDropdown(SCHEDULE_TYPE_ENUM, 'scheduleType'));
    document.getElementById('editMisfirePolicyContainer').appendChild(createDropdown(MISFIRE_ENUM, 'misfirePolicy'));

    function renderSchedules(schedules) {
        schedulesTable.innerHTML = '';
        schedules.forEach(s => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${s.id}</td>
                <td>${s.jobId}</td>
                <td>${s.scheduleType}</td>
                <td>${s.cronExpr || '-'}</td>
                <td>${s.intervalSec ?? '-'}</td>
                <td>${s.startAt ? new Date(s.startAt).toLocaleString() : '-'}</td>
                <td>${s.endAt ? new Date(s.endAt).toLocaleString() : '-'}</td>
                <td>${s.enabled}</td>
                <td>${s.misfirePolicy}</td>
                <td>
                    <button class="edit-btn" data-id="${s.id}">Edit</button>
                    <button class="enable-btn" data-id="${s.id}">Enable</button>
                    <button class="disable-btn" data-id="${s.id}">Disable</button>
                    <button class="delete-btn" data-id="${s.id}">Delete</button>
                </td>
            `;
            schedulesTable.appendChild(tr);
        });

        document.querySelectorAll('.edit-btn').forEach(btn => btn.addEventListener('click', () => openEditModal(btn.dataset.id)));
        document.querySelectorAll('.enable-btn').forEach(btn => btn.addEventListener('click', () => toggleSchedule(btn.dataset.id, true)));
        document.querySelectorAll('.disable-btn').forEach(btn => btn.addEventListener('click', () => toggleSchedule(btn.dataset.id, false)));
        document.querySelectorAll('.delete-btn').forEach(btn => btn.addEventListener('click', () => deleteSchedule(btn.dataset.id)));
    }

    function validateScheduleForm(fd) {
        const type = fd.get('scheduleType');
        const cronExpr = fd.get('cronExpr');
        const intervalSec = fd.get('intervalSec');

        if (type === 'CRON' && !cronExpr) return 'Cron expression is required for CRON schedules';
        if ((type === 'FIXED_RATE' || type === 'FIXED_DELAY') && (!intervalSec || parseInt(intervalSec) <= 0))
            return 'Interval (sec) is required and must be > 0 for FIXED_RATE / FIXED_DELAY schedules';
        return null;
    }

    function formDataToPayload(fd) {
        return {
            scheduleType: fd.get('scheduleType'),
            cronExpr: fd.get('cronExpr') || null,
            intervalSec: fd.get('intervalSec') ? parseInt(fd.get('intervalSec')) : null,
            startAt: fd.get('startAt') ? new Date(fd.get('startAt')).toISOString() : null,
            endAt: fd.get('endAt') ? new Date(fd.get('endAt')).toISOString() : null,
            timezone: fd.get('timezone') || null,
            misfirePolicy: fd.get('misfirePolicy'),
            enabled: fd.get('enabled') === 'on'
        };
    }

    createScheduleForm.addEventListener('submit', async e => {
        e.preventDefault();
        const jobId = jobIdInput.value.trim();
        if (!jobId) return alert('Job ID required');

        const fd = new FormData(createScheduleForm);
        const error = validateScheduleForm(fd);
        if (error) return alert(error);

        try {
            const res = await fetch(`${BASE_URL}/create/${jobId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formDataToPayload(fd))
            });
            const data = await res.json();
            if (data.success) {
                createScheduleForm.reset();
                fetchSchedules();
            } else alert(data.message || 'Failed to create schedule');
        } catch (err) {
            console.error(err);
            alert('Error creating schedule');
        }
    });

    async function fetchSchedules() {
        try {
            const res = await fetch(BASE_URL);
            const data = await res.json();
            if (data.success && data.data.length > 0) renderSchedules(data.data);
            else schedulesTable.innerHTML = '<tr><td colspan="10">No schedules found</td></tr>';
        } catch (err) {
            console.error(err);
            schedulesTable.innerHTML = '<tr><td colspan="10">Error fetching schedules</td></tr>';
        }
    }

    async function toggleSchedule(id, enable) {
        try {
            const res = await fetch(`${BASE_URL}/${id}/${enable ? 'enable' : 'disable'}`, { method: 'PATCH' });
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            fetchSchedules();
        } catch (err) { console.error(err); alert('Failed to update schedule'); }
    }

    async function deleteSchedule(id) {
        if (!confirm('Delete this schedule?')) return;
        try {
            const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            fetchSchedules();
        } catch (err) { console.error(err); alert('Failed to delete schedule'); }
    }

    function openEditModal(id) {
        fetch(`${BASE_URL}/${id}`)
            .then(res => res.json())
            .then(data => {
                if (!data.success) return alert('Failed to fetch schedule');
                const s = data.data;
                editScheduleForm.id.value = s.id;
                editScheduleForm.scheduleType.value = s.scheduleType;
                editScheduleForm.cronExpr.value = s.cronExpr || '';
                editScheduleForm.intervalSec.value = s.intervalSec ?? '';
                editScheduleForm.startAt.value = s.startAt ? new Date(s.startAt).toISOString().slice(0,16) : '';
                editScheduleForm.endAt.value = s.endAt ? new Date(s.endAt).toISOString().slice(0,16) : '';
                editScheduleForm.timezone.value = s.timezone || '';
                editScheduleForm.misfirePolicy.value = s.misfirePolicy;
                editScheduleForm.enabled.checked = s.enabled;
                editModal.style.display = 'block';
            })
            .catch(err => { console.error(err); alert('Failed to fetch schedule'); });
    }

    editScheduleForm.addEventListener('submit', async e => {
        e.preventDefault();
        const fd = new FormData(editScheduleForm);
        const error = validateScheduleForm(fd);
        if (error) return alert(error);

        const id = editScheduleForm.id.value;
        try {
            const res = await fetch(`${BASE_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formDataToPayload(fd))
            });
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            editModal.style.display = 'none';
            fetchSchedules();
        } catch (err) { console.error(err); alert('Failed to update schedule'); }
    });

    document.getElementById('closeModalBtn').addEventListener('click', () => editModal.style.display = 'none');

    // Load all schedules by default
    fetchSchedules();
});
