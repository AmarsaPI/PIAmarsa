# Documentacion tecnica, instalacion y despliegue

## 1. Arquitectura general

PIAmarsa es una aplicacion de gestion de fichajes y horarios compuesta por:

- Backend web: Java 17, Spring Boot, Spring MVC, Spring Security, Spring Data JPA y Thymeleaf.
- Base de datos: PostgreSQL.
- Frontend web: plantillas Thymeleaf, HTML, CSS y JavaScript estatico.
- App movil: Android/Kotlin con Jetpack Compose y Retrofit.
- Generacion documental: OpenPDF para exportar horarios y cuadrantes.
- Documentacion y diseno: diagramas y mockups en `Diagramas`.

## 2. Capas del backend

| Capa | Paquete/carpeta | Responsabilidad |
| --- | --- | --- |
| Controladores web/API | `mvc/controllers` | Recibir peticiones, preparar vistas y exponer endpoints REST |
| Servicios | `mvc/models/services` | Logica de negocio |
| DAO/Repositorios | `mvc/models/dao` | Acceso a datos con Spring Data JPA |
| Entidades | `mvc/models/entity` | Modelo persistente de la aplicacion |
| DTO | `mvc/models/dto` | Objetos de transferencia para vistas/API |
| Configuracion | `mvc/config` | Seguridad y datos iniciales |
| Vistas | `resources/templates` | Pantallas Thymeleaf |
| Estaticos | `resources/static` | CSS, JS e imagenes |

## 3. Modelo de datos

Entidades principales:

- `Empleado`: usuario empleado o administrador con rol, credenciales y relaciones con horarios/fichajes.
- `Admin`: entidad de administracion.
- `Fichaje`: registro de entrada y salida asociado a empleado.
- `Horario`: turno real asignado a empleado. Permite jornada partida mediante `horaInicio2` y `horaFin2`.
- `PlantillaHorario`: plantilla reutilizable de horario.
- `SolicitudCambio`: peticion de modificacion de fichaje u horario.
- `Ausencia`: vacaciones, permisos u otras ausencias.
- `CalendarioLaboral`: calendario de trabajo asociado a festivos.
- `Festivo`: dias no laborables.
- `Contrato`: datos contractuales del empleado, horas semanales y minutos teoricos diarios.

Relaciones destacadas:

- Un empleado puede tener varios fichajes.
- Un empleado puede tener varios horarios.
- Un empleado puede tener varias ausencias y contratos.
- Un calendario laboral puede tener varios festivos.
- Las solicitudes de cambio se vinculan al empleado y al fichaje/horario afectado.
- La bolsa de horas compara horas fichadas contra horas teoricas de turnos/contratos y descuenta ausencias.

## 4. Endpoints principales

| Area | Rutas |
| --- | --- |
| Login | `/login`, `/auth-check`, `/logout` |
| Administracion | `/admin/index`, `/admin/listado_usuarios`, `/admin/agregar_usuario`, `/admin/empleados/guardar` |
| Fichajes web | `/fichar/registrar-entrada`, `/fichar/registrar-salida`, `/historial_fichajes`, `/solicitar-cambio`, `/solicitudes`, `/solicitudes/procesar`, `/fichajes/editar` |
| Fichajes API | `/api/fichajes`, `/api/fichajes/{id}`, `/api/fichajes/empleado/{empleadoId}`, `/api/fichajes/activo/{empleadoId}` |
| Horarios | `/horario_personal`, `/crear_plantilla`, `/asignar_horario`, `/gestion_plantillas` |
| Horarios API | `/api/horarios`, `/api/horarios-reales`, `/api/horarios-reales/global`, `/api/horarios-reales/mis-turnos`, `/api/horarios-reales/mis-festivos`, `/api/horarios/pdf/descargar`, `/api/horarios/pdf/descargar-equipo` |
| Vacaciones | `/vacaciones`, `/vacaciones/solicitar`, `/vacaciones/limpiar-rechazadas`, `/vacaciones/calendario-global`, `/admin/vacaciones/resolver`, `/admin/vacaciones/borrar` |
| Ausencias API | `/api/ausencias`, `/api/ausencias/empleado/{empleadoId}`, `/api/ausencias/verificar-estado` |
| Contratos API | `/api/contratos`, `/api/contratos/empleado/{empleadoId}`, `/api/contratos/activo`, `/api/contratos/{id}` |
| Calendarios | `/admin/calendarios_laborales`, `/admin/agregar_calendario` |
| Convenio | `/convenio`, `/convenio/subir`, `/convenio/descargar` |
| Bolsa de horas | `/bolsa/resumen`, `/bolsa/informe` |

## 5. Cambios funcionales recientes reflejados

- Jornadas partidas en horarios reales y calendario personal.
- Exportacion PDF del horario personal y del cuadrante mensual del equipo.
- Vista global de vacaciones con navegacion mensual, estados visuales y resolucion administrativa.
- Limpieza de solicitudes de vacaciones rechazadas por parte del empleado.
- Contratos por empleado para calcular horas teoricas.
- Bolsa de horas anual acumulada con comparacion entre horas fichadas, turnos, contratos y ausencias.
- Prevencion de duplicados de horarios por empleado y fecha.
- Filtrado mensual del historial de fichajes.

## 6. Instalacion en desarrollo

### Requisitos

- Java 17.
- PostgreSQL.
- Maven o Maven Wrapper funcionando.
- Android Studio y Android SDK para la app movil.

### Base de datos

1. Crear una base de datos PostgreSQL:

```sql
CREATE DATABASE db_almarsa_backend;
```

2. Revisar credenciales en `GestionFichajes/src/main/resources/application.properties`.
3. Arrancar la aplicacion para que Hibernate cree/actualice tablas con `spring.jpa.hibernate.ddl-auto=update`.

### Backend

Desde `GestionFichajes`:

```bash
./mvnw spring-boot:run
```

En Windows:

```bat
mvnw.cmd spring-boot:run
```

Si el wrapper falla, instalar Maven y ejecutar:

```bash
mvn spring-boot:run
```

La aplicacion quedara disponible normalmente en `http://localhost:8080`.

### App movil

1. Abrir `Frontend/App_movil` en Android Studio.
2. Configurar `local.properties` con la ruta del SDK:

```properties
sdk.dir=C:\\Users\\USUARIO\\AppData\\Local\\Android\\Sdk
```

3. Ajustar la URL base de Retrofit para que apunte al backend.
4. Ejecutar en emulador o dispositivo.

## 7. Despliegue

### Preparacion

1. Crear una base de datos PostgreSQL en el servidor.
2. Configurar variables de entorno para credenciales.
3. Desactivar logs SQL de depuracion.
4. Revisar `SecurityConfig` para proteger rutas administrativas y formularios.
5. Generar el artefacto:

```bash
mvn clean package
```

### Ejecucion en servidor

```bash
java -jar target/GestionFichajes-0.0.1-SNAPSHOT.jar
```

### Recomendaciones de produccion

- Usar HTTPS.
- No almacenar passwords en `application.properties`.
- Restringir endpoints por rol.
- Activar CSRF para formularios web.
- Configurar copias de seguridad de PostgreSQL.
- Monitorizar logs y errores.

## 8. Estructura del repositorio

```text
PIAmarsa/
  GestionFichajes/              Backend Spring Boot
  Frontend/Web/                 Prototipo web estatico
  Frontend/App_movil/           App Android
  Frontend/Codigo_capturas/     Capturas y codigo de apoyo
  Diagramas/                    Casos de uso, clases, actividades y despliegue
  Fichajes.postman_collection.json
  Documentacion/Fase5/
```
