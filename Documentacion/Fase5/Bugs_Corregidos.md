# Bugs corregidos durante el desarrollo

Este listado recoge incidencias corregidas o mitigadas durante el desarrollo, deducidas de la implementacion actual del proyecto y de los comentarios del codigo.

| ID | Bug o incidencia | Correccion aplicada | Evidencia |
| --- | --- | --- | --- |
| BUG-01 | Un empleado podia intentar iniciar una nueva jornada teniendo otra entrada abierta del mismo dia | Se anadio validacion para bloquear entradas duplicadas si existe un fichaje activo de hoy | `FichajeServiceImpl.registrarEntrada`, `IFichajeDAO.findFirstByEmpleadoIdAndFechaSalidaIsNullOrderByIdDesc` |
| BUG-02 | Al cerrar una jornada ya cerrada podia sobrescribirse la salida o dejar datos incoherentes | Se valida que el fichaje no tenga salida antes de registrar una nueva | `FichajeServiceImpl.registrarSalida` |
| BUG-03 | El historial de fichajes podia cargar todos los registros sin control de mes | Se anadio filtrado mensual y navegacion por mes anterior/posterior | `FichajeController.mostrarHistorial`, `IFichajeDAO.findByEmpleadoAndMonth` |
| BUG-04 | El listado de fichajes por empleado dependia de una consulta DAO incorrecta o poco precisa | Se usa el metodo corregido por relacion JPA `findByEmpleado_Id` | `FichajeServiceImpl.findByEmpleado` |
| BUG-05 | Las solicitudes de vacaciones podian solaparse con ausencias pendientes o aprobadas | Se anadio comprobacion de colision de fechas antes de guardar la solicitud | `VacacionesController.procesarSolicitud` |
| BUG-06 | Una solicitud de vacaciones podia tener fecha de inicio posterior a fecha fin | Se anadio validacion cronologica y mensaje de error al usuario | `VacacionesController.procesarSolicitud` |
| BUG-07 | El calendario de vacaciones no diferenciaba correctamente estados o tipos de ausencia | Se asignan clases visuales distintas para pendientes, rechazadas, aprobadas, baja medica y permiso retribuido | `VacacionesController.verCalendarioGlobal` |
| BUG-08 | Las solicitudes rechazadas acumuladas podian ensuciar la vista del empleado | Se anadio accion para limpiar solicitudes rechazadas del historial de vacaciones | `VacacionesController.limpiarSolicitudesRechazadas` |
| BUG-09 | Al crear horarios reales podia duplicarse un turno para el mismo empleado y fecha | Se busca un horario existente y se reutiliza su ID para actualizar en lugar de insertar duplicado | `HorarioRestController.guardar` |
| BUG-10 | El cuadrante podia recibir fechas con formato extendido desde FullCalendar y fallar al parsear | Se limpian los parametros `start` y `end` tomando la parte `yyyy-MM-dd` antes de convertir a `LocalDate` | `HorarioRestController.horariosPorEmpleado`, `HorarioRestController.horariosGlobales` |
| BUG-11 | El borrado de turnos de un rango podia eliminar fechas fuera de la seleccion visual | Se interpreta el final de rango como exclusivo, alineado con FullCalendar | `HorarioRestController.eliminarRangoFechas` |
| BUG-12 | Los turnos partidos no se estaban representando completamente en calendarios e informes | Se anadieron campos de segundo tramo horario y calculo de duracion total | `Horario.horaInicio2`, `Horario.horaFin2`, `BolsaHorasServiceImpl.calcularDuracion` |
| BUG-13 | La bolsa de horas podia contar fichajes abiertos como horas trabajadas | Se ignoran fichajes sin entrada o sin salida al calcular duracion | `BolsaHorasServiceImpl.calcularDuracionFichaje` |
| BUG-14 | Los dias con ausencia podian computar horas teoricas de trabajo | Se comprueba si el empleado esta ausente y se devuelve 0 horas comprometidas | `BolsaHorasServiceImpl.obtenerHorasComprometidas` |
| BUG-15 | El panel de administracion podia fallar si la lista de calendarios era nula | Se inicializa una lista vacia cuando no hay calendarios disponibles | `AdminController` |
| BUG-16 | La asignacion manual de ausencias podia aceptar rangos de fechas invalidos | Se valida que la fecha de inicio no sea posterior a la fecha de fin | `AdminController.guardarAusencia` |
| BUG-17 | El acceso a convenio podia llegar a la vista sin usuario de sesion | Se redirige a login si no hay usuario logueado | `ConvenioController` |
| BUG-18 | La descarga de convenio podia quedar cacheada por el navegador | Se anadieron cabeceras de no cache en la respuesta de descarga | `ConvenioController.descargar` |
| BUG-19 | Algunos errores de API devolvian respuestas poco estructuradas | Se anadieron respuestas JSON con `mensaje`, `error` o `errors` en controladores REST y un manejador global | `GlobalExceptionHandler`, controladores REST |
| BUG-20 | La sesion se perdia durante recompilaciones en desarrollo | Se activo persistencia de sesion y timeout de 2 horas en configuracion local | `application.properties` |

## Incidencias conocidas no cerradas

| ID | Incidencia | Estado recomendado |
| --- | --- | --- |
| PEND-01 | La aprobacion de solicitudes de cambio de fichaje aparece incompleta en `SolicitudCambioServiceImpl`; la rama de fichaje no actualiza fechas y puede fallar si `fechaTurno` es nula | Completar antes de produccion o limitar la demo a solicitudes de turno |
| PEND-02 | `SecurityConfig` mantiene muchas rutas sensibles como `permitAll()` | Restringir por roles antes de despliegue real |
| PEND-03 | El login movil usa password en URL | Migrar a `POST /api/login` con cuerpo JSON |
| PEND-04 | Las pruebas automatizadas no se pudieron ejecutar en este entorno por Maven/Android SDK | Corregir entorno y ejecutar antes de la entrega final |

