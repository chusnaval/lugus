// groupCreate.js - handles creating new groups via AJAX

document.addEventListener('DOMContentLoaded', () => {
  const modal = document.getElementById('createGroupModal');
  if (!modal) return;

  const nameInput = document.getElementById('newGroupName');
  const faInput = document.getElementById('newGroupFilmAffinityId');
  const submitBtn = document.getElementById('createGroupSubmitBtn');
  const alertContainer = document.getElementById('createGroupAlertContainer');

  function showAlert(type, message) {
    if (!alertContainer) return;
    alertContainer.innerHTML = `<div class="alert alert-${type} alert-dismissible" role="alert">${escapeHtml(message)}<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>`;
  }

  function escapeHtml(unsafe) {
    return (unsafe || '').toString().replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
  }

  async function createGroup() {
    const name = nameInput.value ? nameInput.value.trim() : '';
    const filmaffinityId = faInput.value ? faInput.value.trim() : '';
    if (!name) { showAlert('warning', 'El nombre del grupo es obligatorio.'); return; }

    submitBtn.disabled = true;
    try {
      const params = new URLSearchParams();
      params.append('name', name);
      if (filmaffinityId) params.append('filmaffinityId', filmaffinityId);

      const resp = await fetch('/lugus/group/createAjax', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params.toString(), credentials: 'same-origin' });
      if (!resp.ok) {
        try { const data = await resp.json(); showAlert('danger', data.message || 'Error creando el grupo'); }
        catch (e) { showAlert('danger', 'Error creando el grupo'); }
        return;
      }

      const data = await resp.json();
      if (data.status === 'ok') {
        showAlert('success', 'Grupo creado correctamente');
        // close modal after short delay
        setTimeout(() => {
          const bsModal = bootstrap.Modal.getInstance(modal);
          if (bsModal) bsModal.hide();
        }, 600);

        // append group card to grid
        location.reload(); // simple way to refresh the list, can be optimized by appending the new card directly
      } else {
        showAlert('danger', data.message || 'Error creando el grupo');
      }

    } catch (e) {
      console.error(e);
      showAlert('danger', 'Error creando el grupo');
    } finally {
      submitBtn.disabled = false;
    }
  }

 

  submitBtn.addEventListener('click', createGroup);

  // allow Enter key on inputs
  [nameInput, faInput].forEach(inp => inp.addEventListener('keydown', (e) => { if (e.key === 'Enter') { e.preventDefault(); createGroup(); } }));

});
