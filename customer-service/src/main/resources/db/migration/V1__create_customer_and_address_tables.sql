CREATE TABLE tb_customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    keycloak_user_id VARCHAR(255),
    email VARCHAR(100) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    cep VARCHAR(8) NOT NULL,
    birth_date DATE NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tb_address (
    id BIGSERIAL PRIMARY KEY,
    contact_name VARCHAR(100) NOT NULL,
    contact_last_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(15) NOT NULL,
    address VARCHAR(150) NOT NULL,
    number VARCHAR(10) NOT NULL,
    neighborhood VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    complement VARCHAR(50),
    customer_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_address_customer
        FOREIGN KEY (customer_id)
        REFERENCES tb_customer (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_customer_keycloak_user_id ON tb_customer(keycloak_user_id);
CREATE INDEX idx_address_customer_id ON tb_address(customer_id);
CREATE INDEX idx_address_cep ON tb_address(cep);