document.addEventListener("DOMContentLoaded", function () {
    // Buscamos los elementos de forma segura
    const btnAyuda = document.querySelector(".help-btn");
    const btnCerrar = document.getElementById("cerrarAyuda");
    const modal = document.getElementById("modalAyuda");

    if (btnAyuda) {
        btnAyuda.addEventListener("click", abrirAyuda);
    }

    if (btnCerrar) {
        btnCerrar.addEventListener("click", cerrarAyuda);
    }

    window.addEventListener("click", function (event) {
        if (modal && event.target === modal) {
            modal.style.display = 'none';
        }
    });
});

function abrirAyuda() {
    const modal = document.getElementById('modalAyuda');
    if (modal) modal.style.display = 'flex';
}

function cerrarAyuda() {
    const modal = document.getElementById('modalAyuda');
    if (modal) modal.style.display = 'none';
}