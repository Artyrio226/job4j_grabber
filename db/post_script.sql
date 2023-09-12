CREATE TABLE IF NOT EXISTS post (
    id SERIAL PRIMARY KEY,
    name text,
    text text,
    link text UNIQUE,
    created timestamp
);