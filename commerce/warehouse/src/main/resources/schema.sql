-- Создаём отдельную схему
CREATE SCHEMA IF NOT EXISTS warehouse;

-- Таблица продуктов
CREATE TABLE warehouse.products (
    product_id UUID PRIMARY KEY,
    width DOUBLE PRECISION NOT NULL CHECK (width >= 1),
    height DOUBLE PRECISION NOT NULL CHECK (height >= 1),
    depth DOUBLE PRECISION NOT NULL CHECK (depth >= 1),
    weight DOUBLE PRECISION NOT NULL CHECK (weight >= 1),
    fragile BOOLEAN NOT NULL
);

-- Таблица остатков товаров на складе
CREATE TABLE warehouse.warehouse_stock (
    product_id UUID PRIMARY KEY REFERENCES warehouse.products(product_id) ON DELETE CASCADE,
    quantity BIGINT NOT NULL CHECK (quantity >= 0)
);

-- Таблица адреса склада (если один склад — одна запись)
CREATE TABLE warehouse.warehouse_address (
    id SERIAL PRIMARY KEY,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house VARCHAR(255) NOT NULL,
    flat VARCHAR(255) NOT NULL
);