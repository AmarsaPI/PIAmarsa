-- EMPLEADOS
INSERT INTO empleados (nombre, email, password, rol) VALUES ('Administrador Sistema', 'admin@empresa.com', '$2a$10$7q9v2xkJQyRrJ5p8XnY6uJ8JzHq0pXh0gJz6Q1KzQqkF0X0p5yq', 'ADMINISTRADOR');
INSERT INTO empleados (nombre, email, password, rol) VALUES ('Juan Perez', 'juan@empresa.com', '$2a$10$7q9v2xkJQyRrJ5p8XnY6uJ8JzHq0pXh0gJz6Q1KzQqkF0X0p5yq', 'EMPLEADO');
INSERT INTO empleados (nombre, email, password, rol) VALUES ('Maria Lopez', 'maria@empresa.com', '$2a$10$7q9v2xkJQyRrJ5p8XnY6uJ8JzHq0pXh0gJz6Q1KzQqkF0X0p5yq', 'EMPLEADO');

-- HORARIOS
INSERT INTO horarios (dia_semana, hora_inicio, hora_fin, empleado_id) VALUES ('MONDAY', '09:00:00', '14:00:00', 1);
INSERT INTO horarios (dia_semana, hora_inicio, hora_fin, empleado_id) VALUES ('TUESDAY', '09:00:00', '14:00:00', 1);

-- FICHAJES
INSERT INTO fichajes (fecha_entrada, fecha_salida, empleado_id) VALUES ('2025-03-01 09:05:00', '2025-03-01 14:00:00', 1);
INSERT INTO fichajes (fecha_entrada, fecha_salida, empleado_id) VALUES ('2025-03-02 08:10:00', NULL, 1);
