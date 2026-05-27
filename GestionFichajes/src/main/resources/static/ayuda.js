function abrirAyuda() {
	document.getElementById('modalAyuda').style.display = 'flex';
}
	
function cerrarAyuda() {
    document.getElementById('modalAyuda').style.display = 'none';
}
	
window.onclick = function(event) {
    const modal = document.getElementById('modalAyuda');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
}