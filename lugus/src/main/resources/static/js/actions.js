document.addEventListener('keydown', function(e) {
	if (e.key === 'Escape') {
		const shown = document.querySelector('.dropdown.show');
		if (shown) {
			const bsDropdown = bootstrap.Dropdown.getInstance(shown.querySelector('[data-bs-toggle="dropdown"]'));
			bsDropdown.hide();
		}
	}
});