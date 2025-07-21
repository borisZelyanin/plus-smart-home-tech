CREATE SCHEMA IF NOT EXISTS delivery;

CREATE TABLE IF NOT EXISTS delivery.address (
    id UUID PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(100) NOT NULL,
    house VARCHAR(50) NOT NULL,
    flat VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS delivery.delivery (
    delivery_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    delivery_state VARCHAR(30) NOT NULL CHECK (
        delivery_state IN (
            'CREATED',
            'IN_PROGRESS',
            'DELIVERED',
            'FAILED',
            'CANCELLED'
        )
    ),
    from_address_id UUID NOT NULL,
    to_address_id UUID NOT NULL,
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN,
    FOREIGN KEY (from_address_id) REFERENCES delivery.address(id) ON DELETE CASCADE,
    FOREIGN KEY (to_address_id) REFERENCES delivery.address(id) ON DELETE CASCADE
);