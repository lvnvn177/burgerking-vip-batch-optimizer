-- reservation_db 초기화 스크립트

USE reservation_db;

DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS seats;

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    available_quantity INT NOT NULL, -- 예약 가능한 수량 (상품)
    version BIGINT DEFAULT 0, -- 낙관적 락을 위한 버전 필드
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reservation_date DATETIME NOT NULL,
    reservation_status VARCHAR(20) NOT NULL,
    product_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE (user_id, product_id, reservation_date), -- 유저별 같은 상품-날짜 중복 예약 방지
);

-- 초기 데이터 삽입 (예시)
-- INSERT INTO products (name, category, description, price, available_quantity, created_at) VALUES
-- ('킹스페셜 버거', 'BURGER', 12000.00, 50, NOW());

