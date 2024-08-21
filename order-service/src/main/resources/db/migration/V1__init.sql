CREATE TABLE orders (
                        id          BIGSERIAL PRIMARY KEY,
                        order_number VARCHAR(255),
                        sku_code     VARCHAR(255) NOT NULL,
                        price        DECIMAL(19, 2),
                        quantity     INTEGER,
                        name         VARCHAR(255)
);