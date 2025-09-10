-- reservation_db 초기화 스크립트

USE reservation_db;

DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS seats;

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL,
    available_quantity INT NOT NULL, -- 예약 가능한 수량 (상품)
    version BIGINT DEFAULT 0, -- 낙관적 락을 위한 버전 필드
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    seat_type VARCHAR(50) NOT NULL,
    location VARCHAR(100),
    is_available BOOLEAN NOT NULL DEFAULT TRUE, -- 좌석 사용 가능 여부
    version BIGINT DEFAULT 0, -- 낙관적 락을 위한 버전 필드
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reservation_date DATETIME NOT NULL,
    reservation_status VARCHAR(20) NOT NULL,
    # Either product_id or seat_id should be set, not both or none for simplicity
    product_id BIGINT,
    seat_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (seat_id) REFERENCES seats(id),
    UNIQUE (user_id, product_id, reservation_date), -- 유저별 같은 상품-날짜 중복 예약 방지
    UNIQUE (user_id, seat_id, reservation_date)     -- 유저별 같은 좌석-날짜 중복 예약 방지
);

-- 초기 데이터 삽입 (예시)
INSERT INTO products (name, category, description, price, available_quantity, created_at) VALUES
('킹스페셜 버거', 'BURGER', '한정 수량 프리미엄 버거', 12000.00, 50, NOW());

INSERT INTO seats (name, seat_type, location, is_available, created_at) VALUES
('창가석 1A', 'VIP', '1층', TRUE, NOW()),
('창가석 1B', 'VIP', '1층', TRUE, NOW());