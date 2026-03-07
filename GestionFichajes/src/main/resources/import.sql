-- EMPLEADOS
-- INSERT INTO empleados (nombre, email, password, rol) VALUES ('Administrador Sistema', 'admin@empresa.com', '$2a$10$7q9v2xkJQyRrJ5p8XnY6uJ8JzHq0pXh0gJz6Q1KzQqkF0X0p5yq', 'ADMINISTRADOR');
INSERT INTO empleados (nombre, email, password, rol) VALUES ('Juan Perez', 'juan@piamarsa.com', '$2a$12$sdzIe2P3rT5arPfa4UcoCOJh8Imq3fB.kw2r5fpvika1FO.gT0Him', 'EMPLEADO');
INSERT INTO empleados (nombre, email, password, rol) VALUES ('Maria Lopez', 'maria@piamarsa.com', '$2a$12$sdzIe2P3rT5arPfa4UcoCOJh8Imq3fB.kw2r5fpvika1FO.gT0Him', 'EMPLEADO');

-- HORARIOS
INSERT INTO horarios (dia_semana, hora_inicio, hora_fin, empleado_id) VALUES ('MONDAY', '09:00:00', '14:00:00', 2);
INSERT INTO horarios (dia_semana, hora_inicio, hora_fin, empleado_id) VALUES ('TUESDAY', '09:00:00', '14:00:00', 3);

-- FICHAJES
INSERT INTO fichajes (fecha_entrada, fecha_salida, empleado_id) VALUES ('2025-03-01 09:05:00', '2025-03-01 14:00:00', 2);
INSERT INTO fichajes (fecha_entrada, fecha_salida, empleado_id) VALUES ('2025-03-02 08:10:00', NULL, 3);
