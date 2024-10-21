-- Inserir novos usuários
INSERT INTO users (email, role, operative) VALUES
('maria.eduarda@example.com', 'ADMIN', 1),
('ana.maria@example.com', 'USER', 1),
('thayna.quinteiro@example.com', 'SUPPORT', 1),
('sarah.santos@example.com', 'USER', 1),
('joao.borges@example.com', 'ADMIN', 1);

-- Inserir novos registros de histórico de acesso
INSERT INTO access_history (id_user, login_datetime, logout_datetime) VALUES
(1, '2024-09-10 08:00:00', '2024-09-10 17:00:00'),
(2, '2024-09-10 09:00:00', '2024-09-10 16:00:00'),
(3, '2024-09-10 08:30:00', '2024-09-10 15:30:00'),
(1, '2024-09-11 08:00:00', '2024-09-11 17:00:00'),
(4, '2024-09-11 10:00:00', '2024-09-11 14:00:00');

-- Inserir novas classes
INSERT INTO classes (course_name, classes, id_user, creation_date, operative) VALUES
('ADMINISTRACAO', 'Class A', 1, '2024-09-01', 1),
('DIGITAL_SOLUTIONS', 'Class B', 2, '2024-09-02', 1),
('MANUFATURA_DIGITAL', 'Class C', 1, '2024-09-03', 0),
('MECATRONICA', 'Class D', 3, '2024-09-04', 1),
('ADMINISTRACAO', 'Class E', 4, '2024-09-05', 1);

-- Inserir novos prédios
INSERT INTO building (building_code, operative) VALUES
('Ca100', 1),
('Ca106', 1),
('Ca126', 1),
('Ca129', 1),
('Ca140', 1),
('Ca147', 1),
('Ca148', 1),
('Ca149', 1),
('Ca150', 1),
('Ca151', 1),
('Ca160', 1),
('Ca165', 1),
('Ca169', 1),
('Ca170', 1),
('Ca180', 1),
('Ca183', 1),
('Ca200', 1),
('Ca204', 1),
('Ca205', 1),
('Ca220', 1),
('Ca300', 1),
('Ca320', 1),
('Ca325', 1),
('Ca340', 1),
('Ca341', 1),
('Ca350', 1),
('Ca360', 1),
('Ca370', 1),
('Ca390', 1),
('Ca400', 1),
('Ca401', 1),
('Ca530', 1),
('Ca536', 1),
('Ca540', 1),
('Ca550', 1),
('Ca551', 1),
('Ca560', 1),
('Ca590', 1),
('Ca600', 1);

-- Inserir novas áreas
INSERT INTO area (area_code, area_name, operative) VALUES
('ETS', 'Engineering Technical School', 1),
('BTC', 'Bosch Training Center', 1),
('BD', 'Bosch Digital', 1),
('PT', 'Power Tools', 1),
('BISB', 'Bosch Integrated Solutions Brazil', 1);

-- Inserir novos Raspberry Pi
INSERT INTO raspberry (id_raspberry, id_building, id_area, operative) VALUES
('RP001', 1, 1, 1),
('RP002', 2, 2, 1),
('RP003', 3, 3, 1),
('RP004', 4, 4, 1),
('RP005', 5, 5, 1);

-- Inserir novos ambientes
INSERT INTO environment (environment_name, id_raspberry, operative) VALUES
('Lab. Soluções Digitais 01', 'RP001', 1),
('Lab. Soluções Digitais 02', 'RP002', 1),
('Lab. Soluções Digitais 03', 'RP003', 1),
('Lab. Soluções Digitais 04', 'RP004', 1),
('Lab. Soluções Digitais 05', 'RP005', 1);

-- Inserir novos postos
INSERT INTO post (post, operative) VALUES
('Mesa 1', 1),
('Mesa 2', 1),
('Mesa 3', 1),
('Mesa 4', 1),
('Mesa 5', 1);

-- Inserir novas localizações
INSERT INTO location (id_post, id_environment) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1);

-- Inserir novos centros de custo
INSERT INTO cost_center (cost_center_name, operative) VALUES
('Cost Center 1', 1),
('Cost Center 2', 1),
('Cost Center 3', 1),
('Cost Center 4', 1),
('Cost Center 5', 1);


-- Inserir novos donos principais
INSERT INTO main_owner (id_owner, id_cost_center, operative) VALUES
('FRG7CA', 1, 1),
('FRG7CB', 2, 1),
('FRG7CC', 3, 1),
('FRG7CD', 4, 1),
('FRG7CE', 5, 1);

-- Inserir novos equipamentos
INSERT INTO equipment (cod_equipment, name_equipment, rfid, type, model, validity, admin_rights, observation, id_location, id_owner, operative) VALUES
('E001', 'Laptop A', 123456789012345, 'Laptop', 'NOTEBOOK_STANDARD', '2025-01-01', 'Admin', 'Good condition', 1, 'FRG7CA',  1),
('E002', 'Desktop B', 234567890123456, 'Desktop', 'NOTEBOOK_STANDARD', '2024-12-31', 'User', 'Needs repair', 2, 'FRG7CB',  1),
('E003', 'Tablet C', 345678901234567, 'Tablet', 'NOTEBOOK_STANDARD', '2026-01-01', 'Admin', 'New', 3, 'FRG7CC',  1),
('E004', 'Monitor D', 456789012345678, 'Monitor', 'NOTEBOOK_STANDARD', '2024-11-30', 'User', 'Old', 4, 'FRG7CD',  1),
('E005', 'Printer E', 567890123456789, 'Printer', 'NOTEBOOK_STANDARD', '2025-05-01', 'User', 'Average condition', 5, 'FRG7CE',  1);

INSERT INTO equipment (cod_equipment, name_equipment, rfid, type, model, validity, admin_rights, observation, id_location, id_owner, operative) VALUES
('E021', 'Desktop 1', 345678901234560, 'Desktop', 'NOTEBOOK_STANDARD', '2025-01-01', 'Admin', 'Good condition', 1, 'FRG7CC',  1),
('E022', 'Desktop 2', 456789012345671, 'Desktop', 'NOTEBOOK_STANDARD', '2024-12-31', 'User', 'Needs repair', 2, 'FRG7CC',  0),
('E023', 'Desktop 3', 567890123456782, 'Desktop', 'NOTEBOOK_STANDARD', '2026-01-01', 'Admin', 'New', 3, 'FRG7CC',  1),
('E024', 'Desktop 4', 678901234567893, 'Desktop', 'NOTEBOOK_STANDARD', '2024-11-30', 'User', 'Old', 4, 'FRG7CC',  1),
('E025', 'Server 1', 789012345678904, 'Server', 'NOTEBOOK_STANDARD', '2025-05-01', 'User', 'Average condition', 5, 'FRG7CC',  0),
('E026', 'Server 2', 890123456789015, 'Server', 'NOTEBOOK_STANDARD', '2025-07-15', 'Admin', 'Good condition', 1, 'FRG7CC',  1),
('E027', 'Printer 1', 901234567890126, 'Printer', 'NOTEBOOK_STANDARD', '2026-02-20', 'User', 'New', 2, 'FRG7CC',  1),
('E028', 'Printer 2', 123456789012358, 'Printer', 'NOTEBOOK_STANDARD', '2024-09-30', 'Admin', 'Needs replacement', 3, 'FRG7CC',  0),
('E029', 'Scanner 1', 234567890123469, 'Scanner', 'NOTEBOOK_STANDARD', '2025-12-01', 'User', 'Good condition', 4, 'FRG7CC',  1),
('E030', 'Scanner 2', 345678901234570, 'Scanner', 'NOTEBOOK_STANDARD', '2024-10-15', 'User', 'Old', 5, 'FRG7CC',  0),
('E031', 'Projector 1', 456789012345681, 'Projector', 'NOTEBOOK_STANDARD', '2025-08-10', 'Admin', 'Good condition', 1, 'FRG7CC',  1),
('E032', 'Projector 2', 567890123456792, 'Projector', 'NOTEBOOK_STANDARD', '2026-03-25', 'User', 'New', 2, 'FRG7CC',  1),
('E033', 'Router 1', 678901234567803, 'Router', 'NOTEBOOK_STANDARD', '2025-07-01', 'Admin', 'Average condition', 3, 'FRG7CC',  1),
('E034', 'Router 2', 789012345678914, 'Router', 'NOTEBOOK_STANDARD', '2024-08-30', 'User', 'Needs repair', 4, 'FRG7CC',  0),
('E035', 'Switch 1', 890123456789025, 'Switch', 'NOTEBOOK_STANDARD', '2025-11-01', 'Admin', 'Good condition', 5, 'FRG7CC',  1),
('E036', 'Switch 2', 901234567890136, 'Switch', 'NOTEBOOK_STANDARD', '2024-12-31', 'User', 'Old', 1, 'FRG7CC',  0),
('E037', 'Monitor 1', 123456789012367, 'Monitor', 'NOTEBOOK_STANDARD', '2025-01-01', 'Admin', 'Good condition', 2, 'FRG7CC',  1),
('E038', 'Monitor 2', 234567890123478, 'Monitor', 'NOTEBOOK_STANDARD', '2026-01-01', 'User', 'Needs repair', 3, 'FRG7CC',  0),
('E039', 'Webcam 1', 345678901234589, 'Webcam', 'NOTEBOOK_STANDARD', '2025-05-01', 'User', 'Average condition', 4, 'FRG7CC',  1),
('E040', 'Webcam 2', 456789012345690, 'Webcam', 'NOTEBOOK_STANDARD', '2024-11-30', 'Admin', 'Good condition', 5, 'FRG7CC',  1),
('E041', 'Headset 1', 567890123456701, 'Headset', 'NOTEBOOK_STANDARD', '2025-08-10', 'User', 'New', 1, 'FRG7CC',  1),
('E042', 'Headset 2', 678901234567812, 'Headset', 'NOTEBOOK_STANDARD', '2026-03-25', 'Admin', 'Needs replacement', 2, 'FRG7CC',  0),
('E043', 'Microphone 1', 789012345678923, 'Microphone', 'NOTEBOOK_STANDARD', '2025-07-01', 'User', 'Good condition', 3, 'FRG7CC',  1),
('E044', 'Microphone 2', 890123456789034, 'Microphone', 'NOTEBOOK_STANDARD', '2024-08-30', 'Admin', 'Old', 4, 'FRG7CC',  0),
('E045', 'Laptop 1', 901234567890145, 'Laptop', 'NOTEBOOK_STANDARD', '2025-11-01', 'User', 'Good condition', 5, 'FRG7CC',  1),
('E046', 'Laptop 2', 123456789012378, 'Laptop', 'NOTEBOOK_STANDARD', '2024-12-31', 'Admin', 'Needs repair', 1, 'FRG7CC',  0);

-- Inserir novos registros de histórico de rastreio
INSERT INTO tracking_history (id_equipment, id_environment,  action, warning, dateTime) VALUES
(1, 1,  'ENTER', 'GREEN', '2024-09-10 08:00:00'),
(2, 2,  'OUT', 'YELLOW', '2024-09-10 09:00:00'),
(3, 3,  'ENTER', 'RED', '2024-09-10 10:00:00'),
(4, 4,  'OUT', 'GREEN', '2024-09-10 11:00:00'),
(5, 5, 'ENTER', 'YELLOW', '2024-09-10 12:00:00');

-- Inserir novos responsáveis
INSERT INTO responsible (responsible_name, edv, id_classes, id_user, operative) VALUES
('Michael Green', 'EDV001', 1, 1, 1),
('Laura White', 'EDV002', 2, 2, 1),
('Peter Brown', 'EDV003', 3, 3, 0),
('Emma Black', 'EDV004', 4, 4, 1),
('James Clark', 'EDV005', 5, 5, 1);

-- Inserir novas associações entre equipamentos e responsáveis
INSERT INTO equipment_responsible (id_equipment, id_responsible, start_usage, end_usage, operative) VALUES
(1, 1, '2024-09-01', NULL, 1),
(2, 2, '2024-09-02', '2024-09-10', 1),
(3, 3, '2024-09-03', NULL, 1),
(4, 4, '2024-09-04', '2024-09-15', 1),
(5, 5, '2024-09-05', NULL, 1);
