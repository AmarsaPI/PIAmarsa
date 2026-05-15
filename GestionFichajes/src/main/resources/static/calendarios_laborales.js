let contador = 0;

function agregarFestivo() {
	const contenedor = document.getElementById('contenedorFestivos');
	const fila = document.createElement('tr');

	fila.innerHTML = `
            <td>
                <input type="date" name="festivos[${contador}].fecha" class="form-control" required>
            </td>
            <td>
                <input type="text" name="festivos[${contador}].descripcion" class="form-control" placeholder="Nombre del festivo" required>
            </td>
            <td>
                <button type="button" class="btn btn-danger" onclick="eliminarFila(this)">×</button>
            </td>
        `;

	contenedor.appendChild(fila);
	contador++;
}

function eliminarFila(btn) {
	btn.closest('tr').remove();
}

// Añadir una fila vacía al empezar
window.onload = agregarFestivo;