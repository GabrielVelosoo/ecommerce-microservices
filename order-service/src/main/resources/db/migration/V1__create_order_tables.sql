CREATE TABLE tb_order (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total NUMERIC(18, 2) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tb_order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price NUMERIC(18, 2) NOT NULL,

    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id)
        REFERENCES tb_order (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_order_user_id ON tb_order(user_id);
CREATE INDEX idx_order_status ON tb_order(status);