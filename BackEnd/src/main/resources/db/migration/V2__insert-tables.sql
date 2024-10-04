-- Inserir novos usuários
INSERT INTO users (email, role, operative) VALUES
('john.doe@example.com', 'ADMIN', 1),
('jane.smith@example.com', 'USER', 1),
('alice.jones@example.com', 'SUPPORT', 1),
('bob.brown@example.com', 'USER', 0),
('carol.white@example.com', 'ADMIN', 1);

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
('B001', 1),
('B002', 0),
('B003', 1),
('B004', 1),
('B005', 0);

-- Inserir novas áreas
INSERT INTO area (area_code, area_name, operative) VALUES
('A001', 'Area 1', 1),
('A002', 'Area 2', 0),
('A003', 'Area 3', 1),
('A004', 'Area 4', 1),
('A005', 'Area 5', 0);

-- Inserir novos Raspberry Pi
INSERT INTO raspberry (id_raspberry, id_building, id_area, operative) VALUES
('RP001', 1, 1, 1),
('RP002', 2, 2, 0),
('RP003', 3, 3, 1),
('RP004', 4, 4, 1),
('RP005', 5, 5, 0);

-- Inserir novos ambientes
INSERT INTO environment (environment_name, id_raspberry, operative) VALUES
('Environment 1', 'RP001', 1),
('Environment 2', 'RP002', 0),
('Environment 3', 'RP003', 1),
('Environment 4', 'RP004', 1),
('Environment 5', 'RP005', 0);

-- Inserir novos postos
INSERT INTO post (post, operative) VALUES
('Post 1', 1),
('Post 2', 0),
('Post 3', 1),
('Post 4', 1),
('Post 5', 0);

-- Inserir novas localizações
INSERT INTO location (id_post, id_environment) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

-- Inserir novos centros de custo
INSERT INTO cost_center (cost_center_name, operative) VALUES
('Cost Center 1', 1),
('Cost Center 2', 0),
('Cost Center 3', 1),
('Cost Center 4', 1),
('Cost Center 5', 0);

-- Inserir novos centros de custo
INSERT INTO cost_center (cost_center_name, operative) VALUES
('Cost Center 1', 1),
('Cost Center 2', 0),
('Cost Center 3', 1),
('Cost Center 4', 1),
('Cost Center 5', 0);

-- Inserir novos donos principais
INSERT INTO main_owner (id_owner, id_cost_center, operative) VALUES
('O001', 1, 1),
('O002', 2, 0),
('O003', 3, 1),
('O004', 4, 1),
('O005', 5, 0);

-- Inserir novos equipamentos
INSERT INTO equipment (id_equipment, name_equipment, rfid, type, model, validity, admin_rights, observation, id_location, id_owner, operative) VALUES
('E001', 'Laptop A', 123456789012345, 'Laptop', 'NOTEBOOK_STANDARD', '2025-01-01', 'Admin', 'Good condition', 1, 'O001',  1),
('E002', 'Desktop B', 234567890123456, 'Desktop', 'DESKTOP_TINK', '2024-12-31', 'User', 'Needs repair', 2, 'O002',  0),
('E003', 'Tablet C', 345678901234567, 'Tablet', 'NOTEBOOK_ENHANCED', '2026-01-01', 'Admin', 'New', 3, 'O003',  1),
('E004', 'Monitor D', 456789012345678, 'Monitor', 'DESKTOP_EXTERNO', '2024-11-30', 'User', 'Old', 4, 'O004',  1),
('E005', 'Printer E', 567890123456789, 'Printer', 'DESKTOP_TINK', '2025-05-01', 'User', 'Average condition', 5, 'O005',  0);

-- Inserir novos registros de histórico de rastreio
INSERT INTO tracking_history (id_equipment, id_environment, rfid, action, warning, dateTime) VALUES
('E001', 1, 123456789012345, 'ENTER', 'GREEN', '2024-09-10 08:00:00'),
('E002', 2, 234567890123456, 'OUT', 'YELLOW', '2024-09-10 09:00:00'),
('E003', 3, 345678901234567, 'ENTER', 'RED', '2024-09-10 10:00:00'),
('E004', 4, 456789012345678, 'OUT', 'GREEN', '2024-09-10 11:00:00'),
('E005', 5, 567890123456789, 'ENTER', 'YELLOW', '2024-09-10 12:00:00');

-- Inserir novos responsáveis
INSERT INTO responsible (responsible_name, edv, id_classes, id_user, operative) VALUES
('Michael Green', 'EDV001', 1, 1, 1),
('Laura White', 'EDV002', 2, 2, 1),
('Peter Brown', 'EDV003', 3, 3, 0),
('Emma Black', 'EDV004', 4, 4, 1),
('James Clark', 'EDV005', 5, 5, 1);

-- Inserir novas associações entre equipamentos e responsáveis
INSERT INTO equipment_responsible (id_equipment, id_responsible, start_usage, end_usage, operative) VALUES
('E001', 1, '2024-09-01', NULL, 1),
('E002', 2, '2024-09-02', '2024-09-10', 0),
('E003', 3, '2024-09-03', NULL, 1),
('E004', 4, '2024-09-04', '2024-09-15', 1),
('E005', 5, '2024-09-05', NULL, 0);

-- Inserir novos registros de log de usuário
INSERT INTO user_log (id_user, altered_table, id_altered, field, description, datetime, action) VALUES
(1, 'Users', '1', 'role', 'Changed role to SUPPORT', '2024-09-10 09:00:00', 'UPDATE'),
(2, 'equipment', 'E002', 'operative', 'Set operative to 0', '2024-09-10 09:30:00', 'UPDATE'),
(3, 'access_history', '3', 'logout_datetime', 'Updated logout time', '2024-09-10 10:00:00', 'UPDATE'),
(4, 'classes', '4', 'operative', 'Set operative to 1', '2024-09-10 10:30:00', 'UPDATE'),
(5, 'responsible', '5', 'end_usage', 'Updated end usage date', '2024-09-10 11:00:00', 'UPDATE');
