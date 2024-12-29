--INSERT INTO TrainerWorkload (username, firstname, lastname, status, duration)
--VALUES
--    ('Ward.Mejia', 'Ward', 'Mejia', TRUE, 40),
--    ('Kathleen.Carr', 'Kathleen', 'Carr', TRUE, 35),
--    ('Coleman.Yates', 'Coleman', 'Yates', FALSE, 0),
--    ('Frazier.Richards', 'Frazier', 'Richards', TRUE, 25),
--    ('Myrna.Estrada', 'Myrna', 'Estrada', FALSE, 10);


-- Insert Trainers
INSERT INTO trainer (username, firstname, lastname, status) VALUES
('Ward.Mejia', 'Ward', 'Mejia', true),
('Kathleen.Carr', 'Kathleen', 'Carr', false),
('Coleman.Yates', 'Coleman', 'Yates', true),
('Frazier.Richards', 'Frazier', 'Richards', false),
('Myrna.Estrada', 'Myrna', 'Estrada', true);

-- Insert Years for Trainers
INSERT INTO w_year (year_number, trainer_id) VALUES
(2023, (SELECT trainer_id FROM trainer WHERE username = 'Ward.Mejia')),
(2024, (SELECT trainer_id FROM trainer WHERE username = 'Ward.Mejia')),
(2023, (SELECT trainer_id FROM trainer WHERE username = 'Kathleen.Carr')),
(2024, (SELECT trainer_id FROM trainer WHERE username = 'Kathleen.Carr')),
(2023, (SELECT trainer_id FROM trainer WHERE username = 'Coleman.Yates')),
(2024, (SELECT trainer_id FROM trainer WHERE username = 'Coleman.Yates')),
(2023, (SELECT trainer_id FROM trainer WHERE username = 'Frazier.Richards')),
(2024, (SELECT trainer_id FROM trainer WHERE username = 'Frazier.Richards')),
(2023, (SELECT trainer_id FROM trainer WHERE username = 'Myrna.Estrada')),
(2024, (SELECT trainer_id FROM trainer WHERE username = 'Myrna.Estrada'));

-- Insert Months with Training Summary Durations
INSERT INTO w_month (month_number, training_summary_duration, year_id) VALUES
-- For Ward Mejia
(1, 15, 1), (2, 10, 1), (3, 20, 1),
(1, 12, 2), (2, 8, 2), (3, 18, 2),

-- For Kathleen Carr
(1, 25, 3), (2, 18, 3), (3, 30, 3),
(1, 20, 4), (2, 15, 4), (3, 25, 4),

-- For Coleman Yates
(1, 10, 5), (2, 5, 5), (3, 15, 5),
(1, 8, 6), (2, 6, 6), (3, 12, 6),

-- For Frazier Richards
(1, 20, 7), (2, 22, 7), (3, 18, 7),
(1, 15, 8), (2, 20, 8), (3, 25, 8),

-- For Myrna Estrada
(1, 10, 9), (2, 8, 9), (3, 12, 9),
(1, 15, 10), (2, 12, 10), (3, 18, 10);