CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL UNIQUE,
    customer_id VARCHAR(255) NOT NULL,
    merchant_id VARCHAR(255) NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    location VARCHAR(255),
    merchant_category VARCHAR(255),
    is_international BOOLEAN
);
