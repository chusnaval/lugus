const exampleModal = document.getElementById('confirmModal')
if (exampleModal) {
  exampleModal.addEventListener('show.bs.modal', event => {
    const button = event.relatedTarget
    const recipient = button.getAttribute('data-bs-whatever')

    const modalBodyInput = exampleModal.querySelector('.modal-footer form')
	modalBodyInput.action += recipient;
  })
}