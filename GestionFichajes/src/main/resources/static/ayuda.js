document.addEventListener("DOMContentLoaded", function () {
    document.querySelector(".help-btn").addEventListener("click", abrirAyuda);
    document.getElementById("cerrarAyuda").addEventListener("click", cerrarAyuda);

    window.addEventListener("click", function (event) {
        const modal = document.getElementById('modalAyuda');
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    });
});

function abrirAyuda() {
    document.getElementById('modalAyuda').style.display = 'flex';
}

function cerrarAyuda() {
    document.getElementById('modalAyuda').style.display = 'none';
}