CREATE TABLE inventory (
                           id SERIAL PRIMARY KEY,
                           sku_code VARCHAR(255) NOT NULL,
                           quantity INTEGER NOT NULL,
                           warehouse_location VARCHAR(255),
                           last_updated TIMESTAMP
);
