# Presentacion, guion de defensa y cierre

## 1. Estructura propuesta de slides

| Slide | Titulo | Contenido clave |
| --- | --- | --- |
| 1 | PIAmarsa | Nombre del proyecto, equipo y objetivo principal |
| 2 | Problema | Gestion dispersa de fichajes, horarios, vacaciones y documentos |
| 3 | Solucion | Plataforma web centralizada + API + app movil |
| 4 | Usuarios | Empleado y administrador/gestor |
| 5 | Funcionalidades | Fichajes, horarios, empleados, vacaciones, calendario, contratos, convenio, informes |
| 6 | Arquitectura | Spring Boot, MVC, JPA, PostgreSQL, Thymeleaf, Android |
| 7 | Base de datos | Entidades principales y relaciones |
| 8 | Demo web | Login, fichaje, horario, historial |
| 9 | Demo administracion | Empleados, plantillas, vacaciones y calendario |
| 10 | App movil | Login y consumo de API REST |
| 11 | Pruebas, bugs y seguridad | Aceptacion, trazabilidad, bugs corregidos, riesgos detectados y mejoras |
| 12 | Conclusiones | Resultados, lecciones aprendidas y evolucion futura |

## 2. Guion de defensa

### Introduccion

"PIAmarsa nace para centralizar la gestion de fichajes y horarios de una empresa en una unica herramienta. La aplicacion permite que los empleados registren su jornada y consulten su informacion, mientras que los administradores gestionan empleados, turnos, contratos, vacaciones, calendarios e informes."

### Problema y objetivos

"Antes de la solucion, estos procesos podian estar repartidos entre hojas de calculo, documentos o herramientas separadas. Nuestro objetivo ha sido crear un sistema unico, mantenible y ampliable."

### Solucion tecnica

"El backend esta desarrollado con Java 17 y Spring Boot siguiendo una arquitectura MVC. La persistencia se realiza con Spring Data JPA sobre PostgreSQL. Las vistas web se construyen con Thymeleaf y se expone una API REST consumida por una app Android hecha con Kotlin, Compose y Retrofit."

### Demo sugerida

1. Entrar en `/login`.
2. Acceder como empleado.
3. Registrar entrada o salida.
4. Consultar horario personal.
5. Consultar historial.
6. Entrar como administrador.
7. Mostrar listado de empleados.
8. Mostrar gestion de horarios/plantillas y un turno partido.
9. Resolver una solicitud o mostrar vacaciones.
10. Descargar PDF de horario/cuadrante.
11. Mostrar convenio o informe de bolsa de horas.

### Cierre

"Como resultado, el proyecto cubre los requisitos principales definidos: autenticacion, fichajes, horarios, empleados, contratos, vacaciones, solicitudes, informes, exportaciones PDF y acceso movil. Como mejora futura, reforzariamos la seguridad por roles, las pruebas automatizadas y el despliegue continuo."

## 3. Distribucion de roles

| Rol | Responsable | Parte |
| --- | --- | --- |
| Presentador 1 | Pendiente de asignar | Problema, objetivos y usuarios |
| Presentador 2 | Pendiente de asignar | Arquitectura, base de datos y tecnologias |
| Presentador 3 | Pendiente de asignar | Demo web y funcionalidades |
| Presentador 4 | Pendiente de asignar | App movil, bugs corregidos, pruebas, seguridad y cierre |

## 4. Cronometria recomendada

| Bloque | Duracion |
| --- | --- |
| Introduccion y problema | 2 min |
| Objetivos y alcance | 2 min |
| Arquitectura y datos | 3 min |
| Demo funcional | 6 min |
| Pruebas, seguridad y mejoras | 3 min |
| Conclusiones | 2 min |
| Margen | 2 min |

Duracion total recomendada: 18-20 minutos.

## 5. Preguntas y respuestas preparadas

| Pregunta posible | Respuesta recomendada |
| --- | --- |
| Por que Spring Boot? | Permite desarrollar rapido una aplicacion empresarial con seguridad, MVC, JPA y despliegue sencillo. |
| Por que PostgreSQL? | Es una base de datos relacional robusta, adecuada para datos estructurados como empleados, fichajes y horarios. |
| Como se protegen las contrasenas? | Se codifican con BCrypt mediante `BCryptPasswordEncoder`. |
| Que mejorariais en seguridad? | Restringir rutas por rol, activar CSRF en formularios, mover credenciales a variables de entorno y cambiar login API a POST. |
| Como se conecta la app movil? | Mediante Retrofit consumiendo endpoints REST expuestos por el backend. |
| Como se validan los requisitos? | Con la matriz de trazabilidad del informe de pruebas finales. |
| Que falta para produccion real? | Hardening de seguridad, tests automatizados completos, HTTPS, CI/CD y configuracion externa. |

## 6. Checklist antes de defender

- Backend arrancado y conectado a PostgreSQL.
- Usuarios de demo comprobados.
- Navegador abierto en `/login`.
- Datos de prueba creados: empleados, horarios, fichajes y vacaciones.
- App movil preparada o capturas disponibles si no hay emulador.
- Documentacion de Fase 5 accesible.
- Demo ensayada con cronometro.
