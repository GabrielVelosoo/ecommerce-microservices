CREATE TABLE tb_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    slug VARCHAR(50) NOT NULL,
    parent_category_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_category_id)
        REFERENCES tb_category (id)
        ON DELETE SET NULL
);

CREATE TABLE tb_product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(350) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(18,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    image_url VARCHAR(255),
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES tb_category (id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_category_slug ON tb_category(slug);
CREATE INDEX idx_category_parent_id ON tb_category(parent_category_id);
CREATE INDEX idx_product_category_id ON tb_product(category_id);
CREATE INDEX idx_product_name ON tb_product(name);