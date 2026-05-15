const menuToggle = document.getElementById("menuToggle");
const mainMenu = document.getElementById("mainMenu");
const subMenu = document.getElementById("subMenu");
if (menuToggle) {
    menuToggle.addEventListener("click", () => {
        if (mainMenu) {
            mainMenu.classList.toggle("menu-open");
        }
        if (subMenu) subMenu.hidden = true;
    });
}


if (subMenu) {
    subMenu.hidden = true;
}

if (mainMenu) {
    mainMenu.addEventListener("click", (event) => {
        const item = event.target.closest("li");
        if (!item || !subMenu) return;

        if (item.dataset.submenu === "schedule") {

            subMenu.hidden = !subMenu.hidden;
        } else {

            subMenu.hidden = true;
        }
    });
}

const homeBtn = document.getElementById('icon-home-btn');
const brandLogos = document.querySelectorAll('.brand-logo');
if (homeBtn) {
    homeBtn.addEventListener('click', () => {
        // Ojo: En Spring Boot mejor usar la ruta raíz "/" o "/admin/index"
        window.location.href = '/admin/index'; 
    });
    homeBtn.style.cursor = 'pointer';
}
