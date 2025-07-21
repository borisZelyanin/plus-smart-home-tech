CREATE SCHEMA IF NOT EXISTS "order_schema";

CREATE TABLE IF NOT EXISTS "order_schema".orders (
    order_id UUID PRIMARY KEY,
    shopping_cart_id UUID,
    payment_id UUID,
    delivery_id UUID,
    state VARCHAR(50),
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN,
    total_price DOUBLE PRECISION,
    delivery_price DOUBLE PRECISION,
    product_price DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS "order_schema".order_products (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_products_order FOREIGN KEY (order_id)
        REFERENCES "order_schema".orders(order_id) ON DELETE CASCADE
);