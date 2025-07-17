CREATE SCHEMA IF NOT EXISTS store;

-- Создание расширения для UUID (выполняется один раз)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Таблица товаров в схеме store
CREATE TABLE IF NOT EXISTS store.products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_src TEXT,
    quantity_state VARCHAR(20) NOT NULL CHECK (quantity_state IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),
    product_state VARCHAR(20) NOT NULL CHECK (product_state IN ('ACTIVE', 'DEACTIVATE')),
    product_category VARCHAR(20) NOT NULL CHECK (product_category IN ('LIGHTING', 'CONTROL', 'SENSORS')),
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 1)
);

-- Индекс для поиска по категории
CREATE INDEX IF NOT EXISTS idx_products_category ON store.products(product_category);