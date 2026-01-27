-- V7__seed_artists_albums.sql
-- Seed inicial de artistas, álbuns e gêneros conforme especificação do projeto

-- GÊNEROS
INSERT INTO genre (name)
VALUES
    ('Rock'),
    ('Metal'),
    ('Hip Hop'),
    ('Sertanejo')
ON CONFLICT (name) DO NOTHING;

-- ARTISTAS
INSERT INTO artist (name, type)
VALUES
    ('Serj Tankian', 'SOLO'),
    ('Mike Shinoda', 'SOLO'),
    ('Michel Teló', 'SOLO'),
    ('Guns N’ Roses', 'BAND');

-- ÁLBUNS
INSERT INTO album (title, release_year)
VALUES
    -- Serj Tankian
    ('Harakiri', 2012),
    ('Black Blooms', 2021),
    ('The Rough Dog', 2013),

    -- Mike Shinoda
    ('The Rising Tied', 2005),
    ('Post Traumatic', 2018),
    ('Post Traumatic EP', 2018),
    ('Where’d You Go', 2006),

    -- Michel Teló
    ('Bem Sertanejo', 2014),
    ('Bem Sertanejo - O Show (Ao Vivo)', 2015),
    ('Bem Sertanejo - (1ª Temporada) - EP', 2014),

    -- Guns N’ Roses
    ('Use Your Illusion I', 1991),
    ('Use Your Illusion II', 1991),
    ('Greatest Hits', 2004);

-- ARTIST - ALBUM (N:N)
INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id
FROM artist a
JOIN album al ON
    (a.name = 'Serj Tankian' AND al.title IN ('Harakiri', 'Black Blooms', 'The Rough Dog'))
    OR (a.name = 'Mike Shinoda' AND al.title IN ('The Rising Tied', 'Post Traumatic', 'Post Traumatic EP', 'Where’d You Go'))
    OR (a.name = 'Michel Teló' AND al.title IN (
        'Bem Sertanejo',
        'Bem Sertanejo - O Show (Ao Vivo)',
        'Bem Sertanejo - (1ª Temporada) - EP'
    ))
    OR (a.name = 'Guns N’ Roses' AND al.title IN (
        'Use Your Illusion I',
        'Use Your Illusion II',
        'Greatest Hits'
    ));

-- ARTIST - GENRE (N:N)
INSERT INTO artist_genre (artist_id, genre_id)
SELECT a.id, g.id
FROM artist a
JOIN genre g ON
    (a.name IN ('Serj Tankian', 'Guns N’ Roses') AND g.name IN ('Rock', 'Metal'))
    OR (a.name = 'Mike Shinoda' AND g.name = 'Hip Hop')
    OR (a.name = 'Michel Teló' AND g.name = 'Sertanejo');
