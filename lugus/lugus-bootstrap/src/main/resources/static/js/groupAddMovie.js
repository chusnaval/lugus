// groupAddMovie.js - search and select a single movie to add to a group

document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('addMovieModal');
	const editModal = document.getElementById('editGroupModal');
    if (!modal) return;

    const searchInput = document.getElementById('addMovieSearchInput');
    const groupIdInput = document.getElementById('addMovieGroupId');
    const searchBtn = document.getElementById('addMovieSearchBtn');
    const resultsDiv = document.getElementById('addMovieResults');
	const filmAffinityLink = document.getElementById('filmAffinityLink');
	const editGroupLink = document.getElementById('editGroupBtn');

    // container for alerts
    const alertContainerId = 'addMovieAlertContainer';

    async function search(title, groupId) {
        if (!title || title.trim().length === 0) {
            resultsDiv.innerHTML = '<div class="alert alert-secondary">Introduce un título para buscar.</div>';
            return;
        }

        resultsDiv.innerHTML = `<div class="d-flex justify-content-center py-3"><div class="spinner-border" role="status"><span class="visually-hidden">Cargando...</span></div></div>`;

        try {
            const url = `/lugus/v1/api/groups/` + groupId + `/searchMovies?title=` + encodeURIComponent(title);
            const resp = await fetch(url, { headers: { 'Accept': 'application/json' }, credentials: 'same-origin' });
            if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
            const list = await resp.json();
            renderResults(list);
        } catch (e) {
            console.error(e);
            resultsDiv.innerHTML = `<div class="alert alert-danger">Error buscando películas.</div>`;
        }
    }

    function createAlertHtml(type, message) {
        // type: success, warning, danger, info
        return `<div id="${alertContainerId}" class="alert alert-${type} alert-dismissible" role="alert">${escapeHtml(message)}<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>`;
    }

    function showAlert(type, message) {
        // place alert above results
        const parent = resultsDiv.parentElement;
        // remove existing alert
        const existing = document.getElementById(alertContainerId);
        if (existing) existing.remove();
        parent.insertAdjacentHTML('afterbegin', createAlertHtml(type, message));
    }

    function renderResults(list) {
        if (!list || list.length === 0) {
            resultsDiv.innerHTML = '<div class="alert alert-warning">No se han encontrado coincidencias.</div>';
            return;
        }

        const html = list.map(item => `
      <div class="d-flex align-items-center border-bottom py-2">
        <div class="flex-grow-1">
          <div><strong>${escapeHtml(item.titulo ?? item.title ?? item.nombre ?? '')}</strong> ${item.anyo ? '(' + item.anyo + ')' : ''}</div>
          <div class="text-muted small">id: ${item.id ?? item.tconst ?? ''}</div>
        </div>
        <div>
          <button class="btn btn-sm btn-primary select-movie-btn" data-movie-id="${item.id ?? item.tconst}" data-tipo-id="${item.tipo}">Seleccionar</button>
        </div>
      </div>
    `).join('');

        resultsDiv.innerHTML = html;

        // attach click handlers
        Array.from(resultsDiv.querySelectorAll('.select-movie-btn')).forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const movieId = e.currentTarget.getAttribute('data-movie-id');
                const tipoId = e.currentTarget.getAttribute('data-tipo-id');
                await selectMovieAjax(movieId, tipoId, groupIdInput.value);
            });
        });
    }

    async function selectMovieAjax(movieId, tipoId, groupId) {
        try {
            const url = `/lugus/group/` + groupId + `/addMovieAjax`;
            const params = new URLSearchParams();
            params.append('movieId', movieId);
            params.append('tipo', tipoId);

            const resp = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params.toString(), credentials: 'same-origin' });
            if (!resp.ok) {
                // try to parse response body for message
                try {
                    const data = await resp.json();
                    showAlert('danger', data.message || 'Error al añadir la película');
                } catch (ee) {
                    showAlert('danger', 'Error al añadir la película');
                }
                return;
            }

            const data = await resp.json();
            if (data.status === 'ok') {
                showAlert('success', data.message || 'Película añadida');
                // Optionally mark the selected button as disabled to prevent re-adding
                const btn = resultsDiv.querySelector(`button[data-movie-id="${movieId}"]`);
                if (btn) {
                    // capture title/anyo from the row to send to listener
                    const row = btn.closest('.d-flex');
                    let titleText = '';
                    let anyo = null;
                    if (row) {
                        const strong = row.querySelector('strong');
                        if (strong) titleText = strong.textContent.trim();
                        const yearMatch = row.textContent.match(/\((\\d{4})\)/);
                        if (yearMatch) anyo = yearMatch[1];
                    }

                    btn.textContent = 'Añadida';
                    btn.classList.remove('btn-primary');
                    btn.classList.add('btn-success');
                    btn.disabled = true;

                    // Trigger a CustomEvent to let the page update the movie list without a full reload
                    const detail = { movieId: movieId, tipo: tipoId, title: titleText, anyo: anyo };
                    try {
                        document.dispatchEvent(new CustomEvent('group:movieAdded', { detail }));
                    } catch (e) {
                        // Older browsers may not support CustomEvent constructor
                        const ev = document.createEvent('CustomEvent');
                        ev.initCustomEvent('group:movieAdded', true, true, detail);
                        document.dispatchEvent(ev);
                    }

                    // Optional global callback hook if the page defines it
                    if (typeof window.onGroupMovieAdded === 'function') {
                        try { window.onGroupMovieAdded(detail); } catch (e) { console.error(e); }
                    }
                }
                // trigger an event or callback here to update the movie list in the group without a full page reload

            } else if (data.status === 'duplicate') {
                showAlert('warning', data.message || 'La película ya existe en el grupo');
            } else {
                showAlert('danger', data.message || 'Error al añadir la película');
            }

        } catch (e) {
            console.error(e);
            showAlert('danger', 'Error añadiendo la película al grupo');
        }
    }

    function escapeHtml(unsafe) {
        return (unsafe || '')
            .toString()
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/\"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    searchBtn.addEventListener('click', () => search(searchInput.value, groupIdInput.value));
    searchInput.addEventListener('keydown', (e) => { if (e.key === 'Enter') { e.preventDefault(); search(searchInput.value, groupIdInput.value); } });

    // When modal opens, focus input and clear previous alerts
    modal.addEventListener('shown.bs.modal', () => { searchInput.focus(); const a = document.getElementById('addMovieAlertContainer'); if (a) a.remove(); });

    // Allow other scripts to update the page when a movie is added (also handle our own dispatched event)
    function appendMovieRow(detail) {
        // detail: { movieId, tipo, title, anyo }
        // find tbody of the group's movies table
        let tbody = document.querySelector('table.table tbody');

        // If no tbody exists, create a table and insert it before the back button
        if (!tbody) {
            const table = document.createElement('table');
            table.className = 'table table-sm';
            tbody = document.createElement('tbody');
            table.appendChild(tbody);

            const backButton = document.getElementById('backButton');
            if (backButton && backButton.parentNode) {
                backButton.parentNode.insertBefore(table, backButton);
            } else {
                // fallback: append at end of body
                document.body.appendChild(table);
            }

            // remove any message about "no hay películas" if present
            document.querySelectorAll('div').forEach(div => {
                if (div.textContent && div.textContent.includes('No hay películas registradas')) {
                    div.remove();
                }
            });

            // remove delete button if exists
            document.querySelectorAll('button').forEach(btn => {
                if (btn.textContent && btn.textContent.includes('Borrar grupo')) {
                    btn.remove();
                }
            });
        }

        const tr = document.createElement('tr');
        const tdIcon = document.createElement('td');
        const icon = document.createElement('i');
        if (detail.tipo === 'local') {
            icon.className = 'fas fa-exclamation-circle text-warning col-1';
        } else {
            icon.className = 'far fa-times-circle text-danger col-1';
        }
        tdIcon.appendChild(icon);

        const tdTitle = document.createElement('td');
        tdTitle.textContent = detail.title || '';
        if (detail.anyo) tdTitle.textContent += ' (' + detail.anyo + ')';

        // hidden inputs to mimic server-side structure (optional, useful for further JS hooks)
        const hid1 = document.createElement('input'); hid1.type = 'hidden'; hid1.value = detail.tipo === 'local' ? detail.movieId : '';
        const hid2 = document.createElement('input'); hid2.type = 'hidden'; hid2.value = detail.tipo === 'imdb' ? detail.movieId : '';
        tdTitle.appendChild(hid1);
        tdTitle.appendChild(hid2);

        tr.appendChild(tdIcon);
        tr.appendChild(tdTitle);

        tbody.appendChild(tr);
    }

    // listen for our custom event to update the main list
    document.addEventListener('group:movieAdded', (e) => {
        const detail = e.detail || {};
        appendMovieRow(detail);
    });

    // attach delete handlers for existing delete buttons and delegate for future ones
    function attachDeleteHandlers() {
        // direct handlers
        document.querySelectorAll('.delete-groupfilm-btn').forEach(btn => {
            if (!btn.dataset.handlerAttached) {
                btn.addEventListener('click', async (e) => {
                    const groupFilmId = e.currentTarget.getAttribute('data-groupfilm-id');
                    await deleteGroupFilmAjax(groupFilmId, groupIdInput.value, e.currentTarget);
                });
                btn.dataset.handlerAttached = 'true';
            }
        });
    }

    async function deleteGroupFilmAjax(groupFilmId, groupId, buttonElem) {
        if (!confirm('¿Seguro que quieres eliminar esta entrada del grupo?')) return;
        try {
            const url = `/lugus/group/` + groupId + `/removeFilmAjax`;
            const params = new URLSearchParams();
            params.append('groupFilmId', groupFilmId);

            const resp = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params.toString(), credentials: 'same-origin' });
            if (!resp.ok) {
                try {
                    const data = await resp.json();
                    showAlert('danger', data.message || 'Error al eliminar la entrada');
                } catch (ee) {
                    showAlert('danger', 'Error al eliminar la entrada');
                }
                return;
            }
            const data = await resp.json();
            if (data.status === 'ok') {
                showAlert('success', data.message || 'Entrada eliminada');
                // remove the row from DOM
                const row = buttonElem.closest('tr');
                if (row) row.remove();

                // test if any rows remain, if not show "no hay películas" message
                // and show the delete group button again (since it was removed when the first movie was added)
                const tbody = document.querySelector('table.table tbody');
                if (tbody && tbody.children.length === 0) {
                     // simpler to just reload the page to reset state, including the "Borrar grupo" button
					 location.reload();
                } 
            } else {
                showAlert('danger', data.message || 'Error al eliminar la entrada');
            }
        } catch (e) {
            console.error(e);
            showAlert('danger', 'Error al eliminar la entrada');
        }
    }

    // call attach on load
    attachDeleteHandlers();

    // also re-attach when modal adds a new row via appendMovieRow
    const originalAppend = appendMovieRow;
    appendMovieRow = function(detail) {
        originalAppend(detail);
        attachDeleteHandlers();
    }

    // also expose a global hook so other code can programmatically add rows
    window.appendGroupMovieRow = appendMovieRow;


    function openGroupFilmAffinity(groupId) {
        const url = `https://www.filmaffinity.com/es/movie-group.php?group-id=` + groupId;
        window.open(url, '_blank');
    } 


    // === Edit group modal handling ===
    const editGroupBtnElem = document.getElementById('editGroupBtn');
    const editGroupName = document.getElementById('editGroupName');
    const editGroupFilmAffinityId = document.getElementById('editGroupFilmAffinityId');
    const editGroupSubmitBtn = document.getElementById('editGroupSubmitBtn');
    const editGroupAlertContainer = document.getElementById('editGroupAlertContainer');

    function showEditAlert(type, message) {
        if (!editGroupAlertContainer) return;
        editGroupAlertContainer.innerHTML = `<div class="alert alert-${type} alert-dismissible" role="alert">${escapeHtml(message)}<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>`;
    }

    async function submitGroupUpdate() {
        if (!editGroupSubmitBtn) return;
        const name = editGroupName ? editGroupName.value.trim() : '';
        const fa = editGroupFilmAffinityId ? editGroupFilmAffinityId.value.trim() : '';
        if (!name) { showEditAlert('warning', 'El nombre del grupo es obligatorio.'); return; }

        editGroupSubmitBtn.disabled = true;
        try {
            const groupId = groupIdInput.value;
            const params = new URLSearchParams();
            params.append('name', name);
            if (fa) params.append('filmaffinityId', fa);

            const resp = await fetch('/lugus/group/' + groupId + '/updateAjax', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params.toString(), credentials: 'same-origin' });
            if (!resp.ok) {
                try { const data = await resp.json(); showEditAlert('danger', data.message || 'Error actualizando el grupo'); }
                catch (e) { showEditAlert('danger', 'Error actualizando el grupo'); }
                return;
            }
            const data = await resp.json();
            if (data.status === 'ok') {
                showEditAlert('success', data.message || 'Grupo actualizado');
                // refresh page to show updated values
                setTimeout(() => { location.reload(); }, 500);
            } else {
                showEditAlert('danger', data.message || 'Error actualizando el grupo');
            }
        } catch (e) {
            console.error(e);
            showEditAlert('danger', 'Error actualizando el grupo');
        } finally {
            editGroupSubmitBtn.disabled = false;
        }
    }

    if (editGroupSubmitBtn) {
        editGroupSubmitBtn.addEventListener('click', (e) => { e.preventDefault(); submitGroupUpdate(); });
    }

    // guard filmAffinityLink to avoid NPE
    if (typeof filmAffinityLink !== 'undefined' && filmAffinityLink && filmAffinityLink.addEventListener) {
        filmAffinityLink.addEventListener('click', async (e) => {
            const groupFilmId = e.currentTarget.getAttribute('data-groupfa-id');
            await openGroupFilmAffinity(groupFilmId);
        });
    }

    // guard editGroupLink similarly (if needed to prefill modal values)
    if (typeof editGroupLink !== 'undefined' && editGroupLink && editGroupLink.addEventListener) {
        // when modal is shown, ensure inputs are populated (Thymeleaf should already set them)
        const editModalElem = document.getElementById('editGroupModal');
        if (editModalElem) {
            editModalElem.addEventListener('shown.bs.modal', () => {
                if (editGroupName) editGroupName.focus();
                const a = document.getElementById('editGroupAlertContainer'); if (a) a.innerHTML = '';
            });
        }
    }

				  
});