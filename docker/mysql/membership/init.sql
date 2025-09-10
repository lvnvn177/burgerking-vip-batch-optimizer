-- membership_db 초기화 스크립트

USE membership_db;

DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS members;

CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    membership_level VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
    last_level_updated_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    order_status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (member_id) REFERENCES members(id)
);

-- 초기 데이터 삽입 (예시)
-- INSERT INTO members (name, email, membership_level, created_at) VALUES
-- ('김철수', 'chulsu.kim@example.com', 'REGULAR', NOW());
-- INSERT INTO orders (member_id, order_date, total_amount, order_status, created_at) VALUES
-- (1, DATE_SUB(NOW(), INTERVAL 20 DAY), 25000.00, 'COMPLETED', NOW());