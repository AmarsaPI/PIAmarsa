// Variables globales de control
var calendar;
var empleadoSeleccionadoId = null; 
var modalBootstrap = null; 
var fechaSeleccionadaSueltar = "";
var empleadoLogueadoId = null;

document.addEventListener('DOMContentLoaded', function() {
    // 1. LEER EL ROL DEL USUARIO E INICIALIZACIÓN DE SEGURIDAD
    var calendarEl = document.getElementById('calendar');
    var usuarioRol = calendarEl ? calendarEl.dataset.rol : 'EMPLEADO'; 
    var esAdmin = (usuarioRol === 'ADMINISTRADOR'); // 🌟 Verdadero si es Admin
	const bodyEl = document.querySelector('body');
	    if (bodyEl && bodyEl.dataset.empleadoId) {
	        empleadoLogueadoId = bodyEl.dataset.empleadoId;
	    }

    var modalElement = document.getElementById('modalDiaSuelto');
    if (modalElement) {
        modalBootstrap = new bootstrap.Modal(modalElement);
        
        modalElement.addEventListener('hidden.bs.modal', function () {
            limpiarCamposHorasModal(); 
        });
    } else {
        console.error("No se encontró el elemento HTML con id 'modalDiaSuelto'");
    }
    
    // 2. CONFIGURACIÓN DEL CALENDARIO BLINDADA POR ROL
    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'es',
        firstDay: 1, 
        selectable: esAdmin,       // 🌟 Si es Empleado, NO puede seleccionar ni arrastrar
        editable: esAdmin,         // 🌟 Si es Empleado, NO puede mover ni alterar turnos
        selectMirror: esAdmin,
        unselectAuto: true,
        displayEventTime: false,
        dayMaxEvents: 4,
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,dayGridWeek'
        },

        views: {
            dayGridMonth: { buttonText: 'Mes' },
            dayGridWeek: { buttonText: 'Semana' }
        },

        // CONTROL DE SELECCIÓN Y ARRASTRE
        select: function(arg) {
            // 🌟 Por seguridad, si de alguna forma llega aquí un empleado, cancelamos
            if (!esAdmin) {
                calendar.unselect();
                return;
            }

            if (!empleadoSeleccionadoId || empleadoSeleccionadoId === "") {
                alert("Por favor, selecciona primero un empleado en el desplegable de la cabecera para poder asignarle turnos.");
                calendar.unselect();
                return;
            }	

            const diferenciaTiempo = new Date(arg.endStr) - new Date(arg.startStr);
            const unDiaEnMilisegundos = 24 * 60 * 60 * 1000;

            if (diferenciaTiempo <= unDiaEnMilisegundos) {
                // Modo día suelto (Abre tu modal)
                fechaSeleccionadaSueltar = arg.startStr; 
                
                const selectCombo = document.getElementById('selectEmpleadoHeader');
                const nombreEmp = selectCombo.options[selectCombo.selectedIndex].text;
                
                document.getElementById('modalNombreEmpleado').value = nombreEmp;
                document.getElementById('modalFechaMostrar').value = arg.startStr.split("-").reverse().join("/");
                
                const selectPlantillaModal = document.getElementById('modalSelectPlantilla');
                if (selectPlantillaModal) {
                    selectPlantillaModal.value = ""; 
                }

                filtrarPlantillasDiariasEnModal();

                if (modalBootstrap) {
                    modalBootstrap.show();
                }
                calendar.unselect();
                
                const btnBorrar = document.getElementById('btnBorrarSemana');
                if (btnBorrar) btnBorrar.style.display = 'none';
            }
            else {
                // Modo rango de fechas (Para asignación masiva)
                document.getElementById('formFechaInicio').value = arg.startStr;
                document.getElementById('formFechaFin').value = arg.endStr; 

                const btnBorrar = document.getElementById('btnBorrarSemana');
                if (btnBorrar) {
                    btnBorrar.style.display = 'block';
                    btnBorrar.innerText = `🗑️ Borrar del ${arg.startStr.split("-").reverse().join("/")} al ${arg.endStr.split("-").reverse().join("/")}`;
                }
            }
        },
		
        // ELIMINAR UN SOLO DÍA AL PULSAR EL EVENTO
        eventClick: function(info) {
            if (info.event.display === 'background') return;

            // 🌟 Si es un EMPLEADO, cancelamos el clic en el turno asignado
            if (!esAdmin) {
                info.jsEvent.preventDefault();
                return;
            }

            const horarioId = info.event.id;
            const fechaFormateada = info.event.startStr; 
            const tituloTurno = info.event.title;

            if (confirm(`¿Estás seguro de que deseas eliminar el horario del día ${fechaFormateada} (${tituloTurno})?`)) {
                fetch(`/api/horarios-reales/${horarioId}`, {
                    method: 'DELETE'
                })
                .then(response => {
                    if (!response.ok) throw new Error("No se pudo eliminar el horario del servidor.");
                    return response.json();
                })
                .then(data => {
                    alert("🗑️ " + data.mensaje);
                    info.event.remove(); 
                })
                .catch(err => {
                    console.error(err);
                    alert("❌ Error al intentar eliminar el horario: " + err.message);
                });
            }
        },

		// Dentro de new FullCalendar.Calendar(...)
		eventSources: [
		    {
		        events: function(fetchInfo, successCallback, failureCallback) {
		            let url = `/api/horarios-reales/global?start=${fetchInfo.startStr}&end=${fetchInfo.endStr}`;
		            if (empleadoSeleccionadoId) url += `&empleadoId=${empleadoSeleccionadoId}`;
		            
		            fetch(url)
		                .then(res => res.json())
		                .then(data => successCallback(data))
		                .catch(err => failureCallback(err));
		        }
		    },
		    {
				events: function(fetchInfo, successCallback, failureCallback) {
				            // Prioridad: 1º ID del selector (Admin), 2º ID del usuario logueado (Empleado)
				            let idParaConsultar = empleadoSeleccionadoId || empleadoLogueadoId;

				            if (!idParaConsultar || idParaConsultar === "null") {
				                successCallback([]); // No hacemos la petición si no tenemos ID
				                return;
				            }

				            fetch(`/api/festivos/eventos?empleadoId=${idParaConsultar}`)
				                .then(res => res.json())
				                .then(data => successCallback(data))
				                .catch(err => failureCallback(err));
				        }
				    }
				]
    });
    
    calendar.render();
	
    // 3. INYECCIÓN DEL DESPLEGABLE EN EL HEADER (SOLO SI EL ELEMENTO EXISTE)
    const selectEmpleado = document.getElementById('selectEmpleadoHeader');
    const toolbarCenter = document.querySelector('.fc-toolbar-title');
    
    // 🌟 Como al Empleado se lo quitamos con Thymeleaf, esta inyección solo ocurrirá para el ADMIN
    if (selectEmpleado && toolbarCenter) {
        const wrapper = document.createElement('div');
        wrapper.style.display = 'flex';
        wrapper.style.alignItems = 'center';
        wrapper.style.gap = '15px';
        
        toolbarCenter.parentNode.insertBefore(wrapper, toolbarCenter);
        wrapper.appendChild(toolbarCenter);
        wrapper.appendChild(selectEmpleado);
    }

    // BOTÓN DE BORRADO MÚLTIPLE
    const btnBorrarSemana = document.getElementById('btnBorrarSemana');
    if (btnBorrarSemana) {
        btnBorrarSemana.addEventListener('click', function() {
            const fechaInicio = document.getElementById('formFechaInicio').value;
            const fechaFin = document.getElementById('formFechaFin').value;

            if (!empleadoSeleccionadoId) {
                alert("❌ Selecciona un empleado primero en la cabecera para saber qué turnos borrar del rango.");
                return;
            }

            if (confirm(`¿Seguro que quieres borrar TODOS los turnos de este empleado en el rango seleccionado?`)) {
                fetch(`/api/horarios-reales/empleado/${empleadoSeleccionadoId}/rango?start=${fechaInicio}&end=${fechaFin}`, {
                    method: 'DELETE'
                })
                .then(response => response.json())
                .then(data => {
                    alert("🗑️ " + data.mensaje);
                    document.getElementById('btnBorrarSemana').style.display = 'none';
                    document.getElementById('formFechaInicio').value = "";
                    document.getElementById('formFechaFin').value = "";
                    calendar.unselect();
                    calendar.refetchEvents();
                })
                .catch(err => {
                    console.error(err);
                    alert("❌ Error al borrar el rango de fechas.");
                });
            } 
        });
    }
	const buscador = document.getElementById('buscadorPlantillasLateral');
	buscador.addEventListener('click', function() {
	    this.value = ""; 
	});
});

// Responde al cambio del selector inyectado en el Header
function seleccionarEmpleadoDesdeHeader(elemento) {
    empleadoSeleccionadoId = elemento.value;
    
    const formEmpleadoInput = document.getElementById('formEmpleadoId');
    if (formEmpleadoInput) {
        formEmpleadoInput.value = empleadoSeleccionadoId;
    }

    console.log("Filtro de visualización cambiado. Empleado ID activo:", empleadoSeleccionadoId);
    
    if (calendar) {
        calendar.refetchEvents();
    }
}

// CARGAR VISTA PREVIA PLANTILLA
function cargarVistaPreviaPlantilla(plantillaId) {
    const previewDiv = document.getElementById('previewPlantilla');
    const listaDias = document.getElementById('listaDiasPreview');
    
    if (!plantillaId || !previewDiv) return; // Salvaguarda si el elemento fue destruido por Thymeleaf

    fetch(`/api/plantillas/${plantillaId}/preview`)
        .then(response => {
            if (!response.ok) throw new Error("No se pudo cargar la plantilla");
            return response.json();
        })
        .then(plantilla => {
            listaDias.innerHTML = ""; 
            const turnos = plantilla.turnos;

            if (!turnos || turnos.length === 0) {
                listaDias.innerHTML = "<li class='text-muted italic'>Plantilla sin turnos definidos</li>";
            } else {
                const nombresDias = ["", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];
                turnos.sort((a, b) => a.diaSemana - b.diaSemana);

                turnos.forEach(t => {
                    const li = document.createElement('li');
                    li.className = "mb-1 border-bottom pb-1 d-flex justify-content-between align-items-center";
                    const inicio = t.horaInicio.substring(0, 5);
                    const fin = t.horaFin.substring(0, 5);
                    const nombreDiaStr = nombresDias[t.diaSemana] || "Día Especial";
                    
                    li.innerHTML = `<span>📅 <strong>${nombreDiaStr}:</strong></span> 
                                        <span class="badge bg-primary shadow-sm">${inicio} - ${fin}</span>`;
                    listaDias.appendChild(li);
                });
            }
            previewDiv.classList.remove('d-none');
        })
        .catch(err => {
            console.error(err);
            listaDias.innerHTML = "<li class='text-danger'>Error al cargar la vista previa</li>";
            previewDiv.classList.remove('d-none');
        });
}

// PROCESAR ASIGNACIÓN MASIVA
function procesarAsignacionMasiva(event) {
    if(event) event.preventDefault(); 

    const selectPlantilla = document.getElementById('selectPlantillas'); 
    const plantillaId = selectPlantilla ? selectPlantilla.value : null;
    
    const fechaInicioStr = document.getElementById('formFechaInicio').value;
    const fechaFinStr = document.getElementById('formFechaFin').value;

    if (!empleadoSeleccionadoId) {
        alert("❌ Error: Selecciona un empleado en la cabecera.");
        return;
    }
    if (!plantillaId || plantillaId === "") {
        alert("❌ Error: Selecciona una plantilla del menú desplegable.");
        return;
    }
    if (!fechaInicioStr || !fechaFinStr) {
        alert("❌ Error: Arrastra primero un rango de fechas sobre el calendario.");
        return;
    }

    fetch(`/api/plantillas/${plantillaId}/preview`)
        .then(response => {
            if (!response.ok) throw new Error("No se pudo obtener el diseño de la plantilla.");
            return response.json();
        })
        .then(plantilla => {
            const turnosTemplate = plantilla.turnos;
            if (!turnosTemplate || turnosTemplate.length === 0) {
                alert("⚠️ Esta plantilla no tiene turnos configurados.");
                return;
            }

            let fechaActual = new Date(fechaInicioStr + "T00:00:00");
            let fechaFin = new Date(fechaFinStr + "T00:00:00");
            let promesasGuardado = [];

            while (fechaActual < fechaFin) {
                let diaSemanaJS = fechaActual.getDay();
                let diaSemanaJava = (diaSemanaJS === 0) ? 7 : diaSemanaJS;

                let turnosDelDia = turnosTemplate.filter(t => parseInt(t.diaSemana) === diaSemanaJava);

                if (turnosDelDia.length > 0) {
                    let año = fechaActual.getFullYear();
                    let mes = String(fechaActual.getMonth() + 1).padStart(2, '0');
                    let dia = String(fechaActual.getDate()).padStart(2, '0');
                    let fechaLimpiaStr = `${año}-${mes}-${dia}`;

                    turnosDelDia.sort((a, b) => a.horaInicio.localeCompare(b.horaInicio));

                    let nuevoHorario = {
                        fecha: fechaLimpiaStr,
                        horaInicio: turnosDelDia[0].horaInicio, 
                        horaFin: turnosDelDia[0].horaFin,
                        horaInicio2: null,
                        horaFin2: null,
                        empleado: { id: parseInt(empleadoSeleccionadoId) }
                    };

                    if (turnosDelDia.length > 1) {
                        nuevoHorario.horaInicio2 = turnosDelDia[1].horaInicio;
                        nuevoHorario.horaFin2 = turnosDelDia[1].horaFin;
                    }

                    let promesa = fetch('/api/horarios-reales', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(nuevoHorario)
                    });

                    promesasGuardado.push(promesa);
                }
                fechaActual.setDate(fechaActual.getDate() + 1);
            }

            if (promesasGuardado.length === 0) {
                alert("ℹ️ Ninguno de los días seleccionados coincide con las jornadas de la plantilla.");
                return;
            }

			Promise.all(promesasGuardado.map(p => 
			                p.then(res => ({ ok: res.ok, status: res.status }))
			                 .catch(() => ({ ok: false }))
			            ))
			            .then(resultados => {
			                const total = resultados.length;
			                const guardados = resultados.filter(r => r.ok).length;
			                const fallidos = total - guardados;

			                if (guardados === total) {
			                    alert("🎉 ¡Cuadrante generado con éxito!");
			                } else if (guardados > 0) {
			                    alert(`⚠️ ¡Cuadrante procesado parcialmente!\n\nSe guardaron ${guardados} turnos.\nNo se pudieron guardar ${fallidos} turnos (posiblemente por conflicto con ausencias).`);
			                } else {
			                    alert("❌ No se pudo guardar ningún turno. Verifica si todos los días seleccionados tienen ausencias.");
			                }

			                calendar.unselect();
			                calendar.refetchEvents();
			            });
        })
        .catch(err => {
            console.error(err);
            alert("❌ Error al procesar la plantilla: " + err.message);
        });
}

// GUARDAR TURNO SUELTO
function guardarTurnoSuelto() {
    const hInicio = document.getElementById('modalHoraInicio').value;
    const hFin = document.getElementById('modalHoraFin').value;
    const hInicio2 = document.getElementById('modalHoraInicio2').value; 
    const hFin2 = document.getElementById('modalHoraFin2').value;

    if(!hInicio || !hFin) {
        alert("Especifica la hora de inicio y de fin.");
        return;
    }

    let nuevoHorarioSuelto = {
        fecha: fechaSeleccionadaSueltar,
        horaInicio: hInicio + ":00",
        horaFin: hFin + ":00",
        horaInicio2: hInicio2 ? hInicio2 + ":00" : null,
        horaFin2: hFin2 ? hFin2 + ":00" : null,
        empleado: { id: parseInt(empleadoSeleccionadoId) }
    };

    fetch('/api/horarios-reales', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevoHorarioSuelto)
    })
    .then(response => {
        if(!response.ok) throw new Error("Error del servidor");
        return response.json();
    })
    .then(data => {
        if (modalBootstrap) {
            modalBootstrap.hide();
        }
        calendar.refetchEvents();
    })
    .catch(err => {
        console.error(err);
        alert("No se pudo guardar el turno individual.");
    });
}

function cargarHorasDesdePlantillaModal(plantillaId) {
    if (!plantillaId || plantillaId === "") {
        limpiarCamposHorasModal();
        return;
    }

    const fechaTexto = document.getElementById('modalFechaMostrar').value;
    if (!fechaTexto) return;
    
    const [dia, mes, anio] = fechaTexto.split('/');
    const fechaObjeto = new Date(`${anio}-${mes}-${dia}`);
    
    let diaSemanaJS = fechaObjeto.getDay();
    let diaSemanaJava = (diaSemanaJS === 0) ? 7 : diaSemanaJS;

    console.log("Buscando turno en plantilla para el día número (Java):", diaSemanaJava);

    fetch(`/api/plantillas/${plantillaId}/preview`)
        .then(response => {
            if (!response.ok) throw new Error("No se pudo obtener el diseño de la plantilla.");
            return response.json();
        })
        .then(plantilla => {
            const turnosTemplate = plantilla.turnos;
            if (!turnosTemplate || turnosTemplate.length === 0) {
                alert("⚠️ Esta plantilla no tiene turnos configurados.");
                return;
            }

            let turnosDelDia = turnosTemplate.filter(t => parseInt(t.diaSemana) === diaSemanaJava);

            if (turnosDelDia.length > 0) {
                turnosDelDia.sort((a, b) => a.horaInicio.localeCompare(b.horaInicio));

                document.getElementById('modalHoraInicio').value = turnosDelDia[0].horaInicio.substring(0, 5);
                document.getElementById('modalHoraFin').value = turnosDelDia[0].horaFin.substring(0, 5);
                
                if (turnosDelDia.length > 1) {
                    document.getElementById('modalHoraInicio2').value = turnosDelDia[1].horaInicio.substring(0, 5);
                    document.getElementById('modalHoraFin2').value = turnosDelDia[1].horaFin.substring(0, 5);
                } else {
                    document.getElementById('modalHoraInicio2').value = "";
                    document.getElementById('modalHoraFin2').value = "";
                }
            } else {
                alert(`Esta plantilla no tiene configurado ningún horario para este día de la semana.`);
                limpiarCamposHorasModal();
            }
        })
        .catch(error => {
            console.error("Error al cargar la plantilla en el modal:", error);
            alert("No se pudo autorellenar el formulario con la plantilla elegida.");
        });
}

function limpiarCamposHorasModal() {
    if(document.getElementById('modalHoraInicio')) document.getElementById('modalHoraInicio').value = "";
    if(document.getElementById('modalHoraFin')) document.getElementById('modalHoraFin').value = "";
    if(document.getElementById('modalHoraInicio2')) document.getElementById('modalHoraInicio2').value = "";
    if(document.getElementById('modalHoraFin2')) document.getElementById('modalHoraFin2').value = "";
    if(document.getElementById('modalSelectPlantilla')) document.getElementById('modalSelectPlantilla').value = "";
}

function filtrarPlantillasDiariasEnModal() {
    const opciones = document.querySelectorAll('.opcion-plantilla');
    if (!opciones.length) return;
    
    opciones.forEach(opcion => {
        const plantillaId = opcion.value;
        
        fetch(`/api/plantillas/${plantillaId}/preview`)
            .then(response => response.json())
            .then(plantilla => {
                const turnos = plantilla.turnos || [];
                const diasUnicos = new Set(turnos.map(t => t.diaSemana));
                
                if (diasUnicos.size > 1 || diasUnicos.size === 0) {
                    opcion.style.display = 'none'; 
                } else {
                    opcion.style.display = 'block'; 
                }
            })
            .catch(err => console.error("Error al filtrar plantilla:", err));
    });
}

function detectarPlantillaSeleccionada(valorEscrito) {
    const opciones = document.querySelectorAll('#listaPlantillasLateral option');
    const inputOcultoId = document.getElementById('selectPlantillas');
    
	let encontrado = false;
    opciones.forEach(opcion => {
        if (opcion.value === valorEscrito) {
            inputOcultoId.value = opcion.getAttribute('data-id');
            encontrado = true;
            cargarVistaPreviaPlantilla(inputOcultoId.value);
        }
    });

    if (!encontrado && inputOcultoId) {
        inputOcultoId.value = "";
        cargarVistaPreviaPlantilla(""); 
    }
}