const menuToggle = document.getElementById("menuToggle");
const mainMenu = document.getElementById("mainMenu");
const subMenu = document.getElementById("subMenu");

if (menuToggle) {
    menuToggle.addEventListener("click", () => {
        if (mainMenu) mainMenu.hidden = !mainMenu.hidden;
        if (subMenu) subMenu.hidden = true;
    });
}

if (mainMenu) {
    mainMenu.addEventListener("click", (event) => {
        const item = event.target.closest("li");
        if (!item || !subMenu) return;
        subMenu.hidden = item.dataset.submenu !== "schedule";
    });
}


const homeBtn = document.getElementById('icon-home-btn');
const brandLogos = document.querySelectorAll('.brand-logo');


homeBtn.addEventListener('click', () => {
    window.location.href = 'index.html';
});
homeBtn.style.cursor = 'pointer';


brandLogos.forEach(logo => {
    logo.addEventListener('click', () => {
        window.location.href = 'index.html';
    });
    logo.style.cursor = 'pointer';
});
