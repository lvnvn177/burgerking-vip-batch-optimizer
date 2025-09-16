-- membership_db 초기화 스크립트

USE membership_db;

DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS members;

CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    grade VARCHAR(20) NOT NULL,
    last_evaluation_date DATETIME NOT NULL,
    next_evaluation_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(255) NOT NULL,
    order_amount INT NOT NULL,
    order_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES members(user_id)
);

-- 초기 데이터 삽입 예시
-- INSERT INTO members (user_id, grade, last_evaluation_date, next_evaluation_date, created_at, updated_at) VALUES
-- (1001, 'BRONZE', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW(), NOW()),
-- (1002, 'SILVER', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW(), NOW()),
-- (1003, 'GOLD', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW(), NOW()),
-- (1004, 'BRONZE', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW(), NOW()),
-- (1005, 'BRONZE', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW(), NOW());

-- INSERT INTO orders (user_id, order_number, order_amount, order_date, created_at) VALUES
-- (1001, 'ORDER-M-001', 15000, DATE_SUB(NOW(), INTERVAL 25 DAY), NOW()),
-- (1001, 'ORDER-M-002', 20000, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
-- (1002, 'ORDER-M-003', 30000, DATE_SUB(NOW(), INTERVAL 20 DAY), NOW()),
-- (1002, 'ORDER-M-004', 10000, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
-- (1003, 'ORDER-M-005', 50000, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
-- (1004, 'ORDER-M-006', 5000, DATE_SUB(NOW(), INTERVAL 30 DAY), NOW()),
-- (1005, 'ORDER-M-007', 12000, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW());