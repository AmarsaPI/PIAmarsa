# Informe de pruebas finales

## 1. Objetivo

Validar que PIAmarsa satisface los requisitos principales de negocio antes de la entrega final: gestion de empleados, fichajes, horarios, vacaciones/ausencias, calendarios laborales, convenio, bolsa de horas, administracion y acceso desde app movil.

## 2. Alcance probado

| Area | Funcionalidades verificadas | Evidencia en el proyecto |
| --- | --- | --- |
| Acceso | Pantalla de login, comprobacion de credenciales y cierre de sesion | `LoginController`, `SecurityConfig`, `login.html` |
| Empleados | Alta, listado, edicion y eliminacion | `AdminController`, `EmpleadoRestController` |
| Fichajes | Registro de entrada/salida, historial, solicitudes de cambio | `FichajeController`, `FichajeRestController` |
| Horarios | Horario personal, plantillas, asignacion, consulta global y exportacion PDF | `HorarioController`, `HorarioRestController`, `PlantillaHorarioRestController` |
| Vacaciones y ausencias | Solicitud, resolucion por administracion y calendario global | `VacacionesController`, `AusenciaRestController` |
| Calendarios laborales | Creacion, edicion y eliminacion de calendarios/festivos | `CalendarioLaboralController`, `FestivoRestController` |
| Contratos | Alta, consulta de contrato activo y eliminacion por API | `ContratoRestController`, `ContratoServiceImpl` |
| Convenio | Subida y descarga de documento PDF | `ConvenioController`, `uploads/documentos/convenio_colectivo.pdf` |
| Bolsa de horas | Resumen e informe de horas | `BolsaHorasController` |
| App movil | Login, consulta de empleados y fichajes via API REST | `Frontend/App_movil`, `ApiService.kt` |

## 3. Pruebas de aceptacion de usuario

Se han definido pruebas funcionales simulando dos perfiles:

- Empleado: consulta sus datos, ficha entrada/salida, revisa historial, consulta horario personal y solicita cambios/vacaciones.
- Administrador/Gestor: gestiona empleados, horarios, plantillas, ausencias, vacaciones, calendarios laborales, convenio e informes.

| ID | Caso de prueba | Pasos | Resultado esperado | Estado |
| --- | --- | --- | --- | --- |
| PA-01 | Login de administrador | Acceder a `/login`, introducir credenciales de demo y validar | Acceso al panel de administracion | Pendiente de validacion manual final |
| PA-02 | Login de empleado | Acceder con usuario empleado | Redireccion a pantalla principal de empleado | Pendiente de validacion manual final |
| PA-03 | Registro de entrada | Pulsar registrar entrada desde la pantalla principal | Se crea fichaje activo sin salida | Cubierto por controlador y API |
| PA-04 | Registro de salida | Con fichaje activo, pulsar registrar salida | Se completa fecha/hora de salida | Cubierto por controlador y API |
| PA-05 | Historial de fichajes | Abrir `/historial_fichajes` | Se muestran fichajes del empleado | Cubierto por vista |
| PA-06 | Solicitud de cambio | Enviar solicitud de modificacion de fichaje/horario | La solicitud queda pendiente para gestion | Cubierto parcialmente; ver incidencias conocidas |
| PA-07 | Gestion de empleados | Crear/editar/eliminar empleado desde administracion | Persistencia correcta en BD | Cubierto por controlador |
| PA-08 | Asignacion de horario | Crear plantilla y asignarla a empleado | El empleado visualiza el horario asignado | Cubierto por controlador y API |
| PA-09 | Solicitud de vacaciones | Enviar solicitud desde `/vacaciones` | La ausencia queda pendiente | Cubierto por controlador |
| PA-10 | Resolucion de vacaciones | Administrador aprueba/rechaza solicitud | Estado actualizado y visible en calendario | Cubierto por controlador |
| PA-11 | Convenio colectivo | Subir y descargar PDF | El documento queda disponible | Cubierto por controlador |
| PA-12 | Consulta desde app movil | Login y consumo de endpoints `/api` | La app recibe datos del backend | Cubierto por Retrofit |
| PA-13 | Turno partido | Crear horario con tramo de manana y tarde | El calendario muestra ambos tramos y la bolsa de horas suma ambos | Cubierto por entidad, API y servicio |
| PA-14 | Exportacion PDF de equipo | Descargar cuadrante mensual | Se genera PDF con empleados y semanas del mes | Cubierto por API |
| PA-15 | Contrato activo | Consultar contrato activo de empleado/fecha | Se devuelven horas semanales y minutos teoricos diarios | Cubierto por API |

## 4. Pruebas de rendimiento final

### Escenarios propuestos

| Escenario | Carga razonable | Criterio de aceptacion |
| --- | --- | --- |
| Login concurrente | 20 usuarios durante 1 minuto | Sin errores 5xx; respuesta inferior a 2 s en entorno local |
| Consulta de horario | 50 peticiones/minuto | Sin errores; datos consistentes |
| Registro de fichaje | 30 operaciones/minuto | No se crean duplicados incoherentes |
| Listado administrativo | 10 gestores consultando listados | Respuesta estable y sin bloqueos |
| Cuadrante global | 50 eventos de horarios/ausencias por consulta | Renderizado sin error y filtrado correcto por rango |
| Exportacion PDF | 10 descargas consecutivas | Generacion sin bloqueo de la aplicacion |

### Resultado

No se ha incluido una herramienta de carga en el repositorio. Para la entrega se recomienda ejecutar los escenarios con JMeter, k6 o Postman Runner usando `Fichajes.postman_collection.json`.

Comando sugerido con k6, si se anade un script de carga:

```bash
k6 run pruebas/rendimiento/fichajes.js
```

## 5. Analisis de seguridad final

| Control | Observacion | Riesgo | Recomendacion |
| --- | --- | --- | --- |
| Hash de contrasenas | Se usa `BCryptPasswordEncoder` | Bajo | Mantener BCrypt |
| CSRF | `csrf.disable()` en `SecurityConfig` | Medio/alto en formularios web | Activar CSRF o justificar su desactivacion solo para API |
| Rutas permitidas | Muchas rutas sensibles aparecen como `permitAll()` | Alto | Restringir `/admin/**`, gestion de horarios y fichajes por rol |
| Credenciales | `application.properties` contiene usuario y password de BD locales | Medio | Usar variables de entorno en despliegue |
| Login API | Endpoint `/api/login/{email}/{password}` envia password en URL | Alto | Cambiar a `POST /api/login` con cuerpo JSON y HTTPS |
| Subida de PDF | Se limita tamano a 25 MB | Medio | Validar extension, tipo MIME y ruta final |
| Logs SQL | `logging.level.org.hibernate.SQL=debug` | Bajo/medio | Desactivar en produccion |

## 6. Matriz de trazabilidad

| Requisito fase 1 | Funcionalidad implementada | Evidencia | Estado |
| --- | --- | --- | --- |
| RF-01 Autenticacion de usuarios | Login web y login API movil | `LoginController`, `EmpleadoRestController` | Cubierto |
| RF-02 Gestion de empleados | CRUD de empleados | `AdminController`, `EmpleadoRestController` | Cubierto |
| RF-03 Registrar entrada y salida | Fichajes web/API | `FichajeController`, `FichajeRestController` | Cubierto |
| RF-04 Consultar historial | Historial de fichajes | `historial_fichajes.html` | Cubierto |
| RF-05 Gestionar horarios | Plantillas y horarios reales | `HorarioController`, `HorarioRestController` | Cubierto |
| RF-06 Consultar horario personal | Vista de horario personal | `horario_personal.html` | Cubierto |
| RF-07 Solicitar cambios | Solicitudes de fichaje/horario | `SolicitudCambio`, `FichajeController`, `HorarioController` | Parcial; pendiente revisar aprobacion de cambios de fichaje |
| RF-08 Gestionar vacaciones/ausencias | Solicitud y resolucion | `VacacionesController`, `AusenciaRestController` | Cubierto |
| RF-09 Gestionar calendarios laborales | CRUD calendarios/festivos | `CalendarioLaboralController`, `FestivoRestController` | Cubierto |
| RF-10 Gestionar convenio | Subida/descarga PDF | `ConvenioController` | Cubierto |
| RF-11 Informes de horas | Bolsa de horas e informe | `BolsaHorasController` | Cubierto |
| RF-12 Acceso movil | App Android con Retrofit | `Frontend/App_movil` | Cubierto |
| RF-13 Gestion de contratos | Contratos por empleado y contrato activo por fecha | `ContratoRestController`, `Contrato` | Cubierto por API |
| RF-14 Exportacion documental | PDF de horario personal y cuadrante mensual de equipo | `HorarioRestController` | Cubierto |

## 7. Bugs corregidos y pendientes

La lista completa de bugs corregidos durante el desarrollo queda documentada en `Bugs_Corregidos.md`.

Pendientes relevantes antes de produccion:

- Completar la aprobacion de solicitudes de cambio de fichaje en `SolicitudCambioServiceImpl`.
- Endurecer la seguridad por roles en `SecurityConfig`.
- Sustituir el login movil con password en URL por un `POST` seguro.

## 8. Ejecucion tecnica de pruebas

Se intentaron ejecutar las pruebas automatizadas disponibles el 25/05/2026:

| Componente | Comando | Resultado |
| --- | --- | --- |
| Backend Spring Boot | `.\mvnw.cmd test` | No ejecutado: el wrapper falla con `No se puede indizar en una matriz nula` y `Cannot start maven from wrapper` |
| Backend Spring Boot | `mvn test` | No ejecutado: Maven no esta instalado en el entorno |
| App Android | `.\gradlew.bat test` | No ejecutado: falta `ANDROID_HOME` o `local.properties` con `sdk.dir` |

Estas incidencias no invalidan la documentacion funcional, pero deben resolverse antes de una entrega con integracion continua.
