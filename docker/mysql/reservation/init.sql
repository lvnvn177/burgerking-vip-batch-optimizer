-- reservation_db 초기화 스크립트

USE reservation_db;

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS stores;

CREATE TABLE stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    is_open BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    store_id BIGINT,
    available BOOLEAN NOT NULL,
    available_quantity INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (store_id) REFERENCES stores(id)
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    pickup_time DATETIME NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (store_id) REFERENCES stores(id)
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (menu_id) REFERENCES products(id)
);

-- -- 초기 데이터 삽입 (예시)
-- INSERT INTO stores (name, is_open, created_at) VALUES
-- ('강남점', TRUE, NOW()),
-- ('홍대점', TRUE, NOW());

-- INSERT INTO products (name, category, price, store_id, available, available_quantity, created_at) VALUES
-- ('와퍼', 'BURGER', 8000.00, 1, TRUE, 100, NOW()),
-- ('불고기버거', 'BURGER', 7000.00, 1, TRUE, 100, NOW()),
-- ('치즈버거', 'BURGER', 6000.00, 2, TRUE, 100, NOW());

