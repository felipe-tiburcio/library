INSERT INTO
    books (title, author, isbn, available)
SELECT
    'title-' || g,
    'author-' || g,
    '978' || lpad(g :: text, 10, '0'),
    g % 5 <> 0
FROM
    generate_series(1, 1000000) AS g ON CONFLICT (isbn) DO NOTHING;