-- Схема (если нужно изолировать)
CREATE SCHEMA IF NOT EXISTS cart;

-- UUID генератор (если нужен)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Таблица корзин
CREATE TABLE IF NOT EXISTS cart.shopping_cart (
    cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT now()
);

-- Таблица товаров в корзине
CREATE TABLE IF NOT EXISTS cart.shopping_cart_item (
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 0),

    PRIMARY KEY (cart_id, product_id),
    FOREIGN KEY (cart_id) REFERENCES cart.shopping_cart(cart_id) ON DELETE CASCADE
    -- product_id будет связан со складом позже
);