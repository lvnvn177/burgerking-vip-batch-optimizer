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
-- 초기 데이터 삽입
INSERT INTO stores (name, is_open, created_at, updated_at) VALUES
('강남점', TRUE, NOW(), NOW()),
('홍대점', TRUE, NOW(), NOW());

INSERT INTO products (name, category, price, store_id, available, available_quantity, created_at, updated_at) VALUES
('와퍼', 'BURGER', 8000.00, 1, TRUE, 100, NOW(), NOW()),
('불고기버거', 'BURGER', 7000.00, 1, TRUE, 100, NOW(), NOW()),
('새우와퍼', 'BURGER', 8500.00, 1, TRUE, 50, NOW(), NOW()),
('치즈버거', 'BURGER', 6000.00, 2, TRUE, 100, NOW(), NOW()),
('롱치킨버거', 'BURGER', 7500.00, 2, TRUE, 70, NOW(), NOW());

INSERT INTO orders (user_id, store_id, order_number, pickup_time, total_amount, status, created_at, updated_at) VALUES
(1001, 1, 'RES-001', DATE_ADD(NOW(), INTERVAL 30 MINUTE), 16500.00, 'PENDING', NOW(), NOW()),
(1002, 1, 'RES-002', DATE_ADD(NOW(), INTERVAL 45 MINUTE), 8000.00, 'PENDING', NOW(), NOW()),
(1003, 2, 'RES-003', DATE_ADD(NOW(), INTERVAL 60 MINUTE), 6000.00, 'PENDING', NOW(), NOW());

INSERT INTO order_items (order_id, menu_id, quantity, price) VALUES
(1, 1, 1, 8000.00),
(1, 3, 1, 8500.00),
(2, 1, 1, 8000.00),
(3, 4, 1, 6000.00);

