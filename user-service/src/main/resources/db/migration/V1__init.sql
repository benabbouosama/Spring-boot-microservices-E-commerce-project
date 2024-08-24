CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(20),
                       address_line1 VARCHAR(255),
                       address_line2 VARCHAR(255),
                       city VARCHAR(100),
                       state VARCHAR(100),
                       postal_code VARCHAR(20),
                       country VARCHAR(100)
);
