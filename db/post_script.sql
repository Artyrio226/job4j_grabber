CREATE TABLE IF NOT EXISTS post (
    id SERIAL PRIMARY KEY,
    name text,
    link text UNIQUE,
    text text,
    created timestamp
);