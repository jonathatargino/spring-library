INSERT INTO autores (id, nome, nacionalidade, ano_nascimento) VALUES
    (1, 'Machado de Assis',          'Brasileiro',   1839),
    (2, 'Clarice Lispector',         'Brasileira',   1920),
    (3, 'José Saramago',             'Português',    1922),
    (4, 'Gabriel García Márquez',    'Colombiano',   1927),
    (5, 'Franz Kafka',               'Tcheco',       1883)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('autores', 'id'), MAX(id)) FROM autores;
