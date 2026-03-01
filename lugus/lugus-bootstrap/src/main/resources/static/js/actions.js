document.addEventListener('keydown', function(e) {
	if (e.key === 'Escape') {
		const shown = document.querySelector('.dropdown.show');
		if (shown) {
			const bsDropdown = bootstrap.Dropdown.getInstance(shown.querySelector('[data-bs-toggle="dropdown"]'));
			bsDropdown.hide();
		}
	}
});

function updateSubmenuDirection(submenu) {
	if (!submenu) {
		return;
	}

	const panel = submenu.querySelector(':scope > .dropdown-menu');
	if (!panel) {
		return;
	}

	submenu.classList.remove('submenu-open-left');

	const submenuRect = submenu.getBoundingClientRect();
	const panelWidth = panel.offsetWidth || 220;
	const rightEdge = submenuRect.right + panelWidth + 8;

	if (rightEdge > window.innerWidth) {
		submenu.classList.add('submenu-open-left');
	}
}

function bindSubmenuAutoFlip() {
	const submenus = document.querySelectorAll('.dropdown-submenu');

	submenus.forEach(function(submenu) {
		submenu.addEventListener('mouseenter', function() {
			updateSubmenuDirection(submenu);
		});

		submenu.addEventListener('focusin', function() {
			updateSubmenuDirection(submenu);
		});
	});
}

document.addEventListener('DOMContentLoaded', bindSubmenuAutoFlip);
window.addEventListener('resize', function() {
	document.querySelectorAll('.dropdown-submenu').forEach(updateSubmenuDirection);
});