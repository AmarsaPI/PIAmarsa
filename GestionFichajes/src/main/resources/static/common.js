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

	    // Saber si estaba abierto
	    const wasHidden = submenu.hidden;

	    // Ocultar todos
	    document.querySelectorAll(".submenu-dropdown")
	        .forEach(sub => sub.hidden = true);

	    // Si estaba cerrado → abrirlo
	    submenu.hidden = !wasHidden;
	});
}

const homeBtn = document.getElementById('icon-home-btn');
const brandLogos = document.querySelectorAll('.brand-logo');

if (homeBtn) {
    homeBtn.addEventListener('click', () => {
        window.location.href = '/admin/index';
    });

    homeBtn.style.cursor = 'pointer';
}