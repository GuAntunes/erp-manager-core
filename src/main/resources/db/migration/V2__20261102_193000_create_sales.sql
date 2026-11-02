CREATE TABLE sales (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    discount_amount DECIMAL(19, 4) NOT NULL,
    discount_currency VARCHAR(3) NOT NULL,
    confirmed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_sales_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT chk_sales_status CHECK (status IN ('DRAFT', 'CONFIRMED', 'CANCELLED')),
    CONSTRAINT chk_sales_discount CHECK (discount_amount >= 0)
);

CREATE TABLE sale_lines (
    sale_id UUID NOT NULL,
    line_number INT NOT NULL,
    product_id UUID NOT NULL,
    product_name_snapshot VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    PRIMARY KEY (sale_id, line_number),
    CONSTRAINT fk_sale_lines_sale FOREIGN KEY (sale_id) REFERENCES sales (id),
    CONSTRAINT chk_sale_lines_quantity CHECK (quantity >= 1),
    CONSTRAINT chk_sale_lines_price CHECK (unit_price >= 0)
);

CREATE INDEX idx_sales_created_at ON sales (created_at);
CREATE INDEX idx_sales_status ON sales (status);
