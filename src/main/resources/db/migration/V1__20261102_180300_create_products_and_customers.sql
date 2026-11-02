CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    sku VARCHAR(64) NOT NULL,
    active BOOLEAN NOT NULL,
    price DECIMAL(19, 4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_products_sku UNIQUE (sku),
    CONSTRAINT chk_products_price CHECK (price >= 0)
);

CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(40) NOT NULL,
    email VARCHAR(254) NOT NULL,
    optional_notes VARCHAR(1000),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_products_created_at ON products (created_at);
CREATE INDEX idx_customers_created_at ON customers (created_at);
