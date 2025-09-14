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

-- 초기 데이터 삽입 (예시)
-- INSERT INTO members (name, membership_level, created_at) VALUES
-- ('김철수', '브론즈', NOW());
-- INSERT INTO orders (member_id, order_date, total_amount, order_status, created_at) VALUES
-- (1, DATE_SUB(NOW(), INTERVAL 20 DAY), 25000.00, 'COMPLETED', NOW());