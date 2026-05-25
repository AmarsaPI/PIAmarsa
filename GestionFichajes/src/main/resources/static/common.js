const menuToggle = document.getElementById("menuToggle");
const mainMenu = document.getElementById("mainMenu");

if (menuToggle) {
	menuToggle.addEventListener("click", () => {

		if (mainMenu) {
			mainMenu.hidden = !mainMenu.hidden;
		}

		// Ocultar todos los submenus
		document.querySelectorAll(".submenu-dropdown")
			.forEach(sub => sub.hidden = true);
	});
}

if (mainMenu) {

	mainMenu.addEventListener("click", (event) => {

		const item = event.target.closest("li");

		if (!item) return;

		const submenuId = item.dataset.submenu;

		if (!submenuId || submenuId === "none") return;

		const submenu = document.getElementById(submenuId);

		if (!submenu) return;


		const wasHidden = submenu.hidden;

		// Ocultar todos
		document.querySelectorAll(".submenu-dropdown")
			.forEach(sub => sub.hidden = true);

		// Si estaba cerrado → abrirlo
		submenu.hidden = !wasHidden;
	});
}

const homeBtn = document.getElementById('icon-home-btn');

if (homeBtn) {
	homeBtn.addEventListener('click', () => {
		window.location.href = '/admin/index';
	});

	homeBtn.style.cursor = 'pointer';
}


document.querySelectorAll('.brand-logo').forEach((logo) => {
	if (logo.closest('a[href]')) return; // ya tiene enlace propio
	const destino = document.body.dataset.home || '/index';
	logo.style.cursor = 'pointer';
	logo.addEventListener('click', () => {
		window.location.href = destino;
	});
});

document.addEventListener("click", (event) => {
    const isClickInsideMenu = mainMenu && mainMenu.contains(event.target);
    const isClickInsideToggle = menuToggle && menuToggle.contains(event.target);

    if (!isClickInsideMenu && !isClickInsideToggle) {
        document.querySelectorAll(".submenu-dropdown").forEach(sub => {
            sub.hidden = true;
        });
    }
});