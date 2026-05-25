# Informe final completo

## 1. Resumen del proyecto

Amarsa es una solucion de gestion de fichajes y horarios desarrollada para centralizar en una unica plataforma procesos que normalmente se gestionan con herramientas separadas: registro de jornada, gestion de empleados, turnos, vacaciones, solicitudes de cambio, calendarios laborales, convenio colectivo, contratos e informes de horas.

El producto incluye una aplicacion web basada en Spring Boot y Thymeleaf, una API REST para integracion movil y una app Android en Kotlin/Jetpack Compose.

## 2. Objetivos

- Permitir a los empleados registrar entradas y salidas.
- Facilitar la consulta del horario personal.
- Permitir al administrador gestionar empleados, turnos y plantillas.
- Gestionar vacaciones, ausencias y solicitudes de cambio.
- Consultar historiales e informes de horas.
- Controlar contratos, horas teoricas y bolsa de horas.
- Centralizar documentos importantes como el convenio colectivo.
- Ofrecer acceso movil a datos clave mediante API REST.

## 3. Resultados obtenidos

| Objetivo | Resultado |
| --- | --- |
| Registro de jornada | Implementado mediante controladores web y API de fichajes |
| Gestion de empleados | Implementada en panel administrativo y API |
| Horarios y plantillas | Implementado con vistas, servicios y endpoints REST |
| Vacaciones y ausencias | Implementado con solicitud, resolucion, limpieza de rechazadas y calendario global |
| Calendarios laborales | Implementado con CRUD administrativo |
| Convenio | Implementado con subida y descarga de PDF |
| Contratos | Implementado por API con contrato activo y horas teoricas |
| Informes | Implementada bolsa de horas, informes y exportacion PDF de horarios/cuadrantes |
| App movil | Implementado proyecto Android con consumo de API |

## 4. Consolidacion de fases

### Fase 1. Analisis

Se identifico la necesidad de una herramienta unificada para gestionar horarios y fichajes. Los requisitos principales fueron autenticacion, fichajes, gestion de empleados, horarios, solicitudes, vacaciones, informes y soporte movil.

### Fase 2. Disenio

Se definieron diagramas de casos de uso, clases, actividades y despliegue disponibles en `Diagramas`. Tambien se prepararon mockups de pantallas para guiar la interfaz.

### Fase 3. Desarrollo inicial

Se construyo el backend Spring Boot con arquitectura MVC, persistencia JPA y vistas Thymeleaf. Se anadieron endpoints REST para la app movil y primeras pantallas web.

### Fase 4. Calidad

Se organizaron funcionalidades completas y se prepararon pruebas funcionales. El proyecto incluye pruebas base generadas para backend y Android, aunque queda pendiente reforzar pruebas automatizadas de negocio.

### Fase 5. Cierre

Se formaliza la documentacion final: pruebas, manual de usuario, documentacion tecnica, instalacion, despliegue, informe final, preparacion de defensa y listado de bugs corregidos.

## 5. Conclusiones

El proyecto cumple el objetivo principal: centralizar la gestion de fichajes y horarios en una aplicacion web con apoyo movil. La arquitectura por capas facilita el mantenimiento y la ampliacion futura. El uso de Spring Boot, JPA y PostgreSQL aporta una base robusta para una aplicacion empresarial de tamano medio.

## 6. Lecciones aprendidas

- La separacion por capas ayuda a localizar errores y evolucionar funcionalidades.
- La gestion de permisos debe definirse pronto para evitar rutas demasiado abiertas al final.
- Las pruebas automatizadas deben crecer junto con la logica de negocio.
- La documentacion tecnica es mas fiable cuando se genera revisando el codigo real.
- La app movil requiere una configuracion de entorno especifica, especialmente Android SDK y URL base del backend.

## 7. Areas de mejora

- Activar proteccion por roles en rutas administrativas.
- Sustituir login API por `POST` con cuerpo JSON y HTTPS.
- Mover credenciales a variables de entorno.
- Anadir tests unitarios y de integracion para servicios principales.
- Incorporar pruebas de carga automatizadas.
- Configurar CI para compilar backend y app movil.
- Completar la rama de aprobacion de solicitudes de cambio de fichaje.

## 8. Bugs corregidos

Durante el desarrollo se corrigieron incidencias relacionadas con fichajes duplicados, jornadas abiertas, filtrado de historial, solapamiento de vacaciones, rangos de fechas invalidos, duplicados de horarios, calculo de jornada partida, bolsa de horas y manejo de errores. El detalle completo esta en `Documentacion/Fase5/Bugs_Corregidos.md`.

## 9. Cierre de entregables

| Entregable | Ubicacion | Estado |
| --- | --- | --- |
| Codigo backend | `GestionFichajes` | Entregado |
| Codigo app movil | `Frontend/App_movil` | Entregado |
| Prototipo web estatico | `Frontend/Web` | Entregado |
| Diagramas | `Diagramas` | Entregado |
| Coleccion Postman | `Fichajes.postman_collection.json` | Entregado |
| Informe de pruebas finales | `Documentacion/Fase5/Informe_Pruebas_Finales.md` | Entregado |
| Manual de usuario | `Documentacion/Fase5/Manual_Usuario.md` | Entregado |
| Documentacion tecnica/despliegue | `Documentacion/Fase5/Documentacion_Tecnica_Instalacion_Despliegue.md` | Entregado |
| Presentacion y guion | `Documentacion/Fase5/Presentacion_Guion_Cierre.md` | Entregado |
| Bugs corregidos | `Documentacion/Fase5/Bugs_Corregidos.md` | Entregado |
