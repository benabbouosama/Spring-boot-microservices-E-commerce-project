-- Add the new 'orderOwner' column
ALTER TABLE orders
    ADD COLUMN order_owner VARCHAR(255);