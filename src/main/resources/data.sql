INSERT INTO
    books (title, author, isbn, available)
VALUES
    (
        'Alice''s Adventures in Wonderland',
        'Lewis Carroll',
        '9780141439761',
        TRUE
    ),
    (
        'The Wonderful Wizard of Oz',
        'L. Frank Baum',
        '9780143454717',
        TRUE
    ),
    (
        'The Adventures of Pinocchio',
        'Carlo Collodi',
        '9780142437442',
        TRUE
    ),
    (
        'Peter Pan',
        'J. M. Barrie',
        '9780142437930',
        TRUE
    ),
    (
        'The Hobbit',
        'J. R. R. Tolkien',
        '9780547928227',
        TRUE
    ) ON CONFLICT (isbn) DO NOTHING;

INSERT INTO
    users (name, email, type)
VALUES
    (
        'Ana Silva',
        'ana.silva@example.com',
        'STUDENT'
    ),
    (
        'Bruno Costa',
        'bruno.costa@example.com',
        'STUDENT'
    ),
    (
        'Carla Oliveira',
        'carla.oliveira@example.com',
        'TEACHER'
    ),
    (
        'Daniel Santos',
        'daniel.santos@example.com',
        'TEACHER'
    ),
    (
        'Alfredo Bartolomeu',
        'alfredo.bartolomeu@example.com',
        'STAFF'
    ) ON CONFLICT (email) DO NOTHING;