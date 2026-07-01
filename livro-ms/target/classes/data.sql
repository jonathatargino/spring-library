INSERT INTO livros (id, titulo, genero, ano_publicacao, disponivel, autor_id) VALUES
    (1, 'Dom Casmurro',                        'Romance',           1899, true,  1),
    (2, 'Memórias Póstumas de Brás Cubas',     'Romance',           1881, false, 1),
    (3, 'A Hora da Estrela',                   'Literatura',        1977, true,  2),
    (4, 'A Paixão Segundo G.H.',               'Literatura',        1964, false, 2),
    (5, 'Ensaio sobre a Cegueira',             'Ficção',            1995, true,  3),
    (6, 'Cem Anos de Solidão',                 'Realismo Mágico',   1967, true,  4),
    (7, 'A Metamorfose',                       'Ficção',            1915, false, 5)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('livros', 'id'), MAX(id)) FROM livros;
