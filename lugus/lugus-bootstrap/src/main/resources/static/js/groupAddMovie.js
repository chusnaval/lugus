// groupAddMovie.js - search and select a single movie to add to a group

document.addEventListener('DOMContentLoaded', () => {
  const modal = document.getElementById('addMovieModal');
  if (!modal) return;

  const searchInput = document.getElementById('addMovieSearchInput');
  const groupIdInput = document.getElementById('addMovieGroupId');
  const searchBtn = document.getElementById('addMovieSearchBtn');
  const resultsDiv = document.getElementById('addMovieResults');

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
          <div><strong>${escapeHtml(item.titulo ?? item.title ?? item.nombre ?? '')}</strong> ${item.anyo ? '('+item.anyo+')' : ''}</div>
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
          btn.textContent = 'Añadida';
          btn.classList.remove('btn-primary');
          btn.classList.add('btn-success');
          btn.disabled = true;
        }
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

});
