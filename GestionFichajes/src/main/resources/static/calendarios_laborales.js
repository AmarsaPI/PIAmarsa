// Gestión dinámica de festivos en el formulario de calendarios
//


function agregarFestivo() {
	const contenedor = document.getElementById('contenedorFestivos');
	const fila = document.createElement('tr');

	fila.innerHTML = `
		<td>
			<input type="hidden" class="festivo-id">
			<input type="date" class="festivo-fecha form-control" required>
		</td>
		<td>
			<input type="text" class="festivo-descripcion form-control" placeholder="Nombre del festivo" required>
		</td>
		<td class="text-center">
			<button type="button" class="btn btn-danger" onclick="eliminarFila(this)">×</button>
		</td>
	`;

	contenedor.appendChild(fila);
	actualizarContador();
}

function eliminarFila(btn) {
	btn.closest('tr').remove();
	actualizarContador();
}

function actualizarContador() {
	const visual = document.getElementById('contadorVisual');
	if (!visual) return;
	const total = document.getElementById('contenedorFestivos').querySelectorAll('tr').length;
	visual.textContent = total;
}


function renumerarYEnviar() {
	const filas = document.getElementById('contenedorFestivos').querySelectorAll('tr');
	filas.forEach((fila, i) => {
		const id = fila.querySelector('.festivo-id');
		const fecha = fila.querySelector('.festivo-fecha');
		const desc = fila.querySelector('.festivo-descripcion');
		if (id) id.name = `festivos[${i}].id`;
		if (fecha) fecha.name = `festivos[${i}].fecha`;
		if (desc) desc.name = `festivos[${i}].descripcion`;
	});
}

window.addEventListener('DOMContentLoaded', () => {
	const form = document.querySelector('form[action*="guardar_calendario"]');
	if (form) form.addEventListener('submit', renumerarYEnviar);

	const contenedor = document.getElementById('contenedorFestivos');
	if (!contenedor) return;


	if (contenedor.querySelectorAll('tr').length === 0) {
		agregarFestivo();
	}
	actualizarContador();
});
