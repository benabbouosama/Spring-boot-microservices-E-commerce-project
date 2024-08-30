CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       password VARCHAR(255)
);
