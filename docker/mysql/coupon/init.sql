-- coupon_db 초기화 스크립트

-- 사용할 데이터베이스 선택 (docker-compose.yml에서 MYSQL_DATABASE로 설정됨)
USE coupon_db;

-- 기존 테이블이 있다면 삭제 (개발용)
DROP TABLE IF EXISTS coupon_issuances;
DROP TABLE IF EXISTS coupon_stocks;
DROP TABLE IF EXISTS coupons;

-- coupons 테이블 생성
CREATE TABLE coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    coupon_type VARCHAR(50) NOT NULL,
    discount_amount DECIMAL(10, 2) NOT NULL,
    is_percentage BOOLEAN NOT NULL,
    minimum_order_amount DECIMAL(10, 2) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    update_At DATETIME
);

-- coupon_stocks 테이블 생성 (coupon_id에 UNIQUE 제약조건 추가)
CREATE TABLE coupon_stocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_id BIGINT NOT NULL UNIQUE,
    total_quantity INT NOT NULL, 
    remaining_quantity INT NOT NULL,
    version BIGINT DEFAULT 0, -- 낙관적 락을 위한 버전 필드
    created_at DATETIME NOT NULL,
    update_at DATETIME,
    FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);


-- coupon_issuances 테이블 생성 (user_id와 coupon_id 조합에 UNIQUE 제약조건 추가)
CREATE TABLE coupon_issuances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    coupon_code VARCHAR(50) NOT NULL UNIQUE, -- 고유한 쿠폰 코드
    status VARCHAR(20) NOT NULL,
    issued_at DATETIME NOT NULL,
    used_at DATETIME,
    expires_at DATETIME NOT NULL,
    order_reference VARCHAR(255),
    FOREIGN KEY (coupon_id) REFERENCES coupons(id),
    UNIQUE (user_id, coupon_id) -- 한 유저는 같은 쿠폰을 한 번만 발급
)

-- 초기 데이터 삽입 (예시)
INSERT INTO coupons (name, description, coupon_type, discount_amount, is_percentage, minimum_order_amount, start_date, end_date, created_at) VALUES
('골든 패티 쿠폰', '기간 한정 골든 패티 무료 쿠폰', 'FREE_MENU', 0.00, FALSE, 0.00, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), NOW());

INSERT INTO coupon_stocks (coupon_id, total_quantity, remaining_quantity, created_at) VALUES
((SELECT id FROM coupons WHERE name = '골든 패티 쿠폰'), 1000, 1000, NOW());

-- 만약 회원도 미리 만들어서 연동한다면 아래와 같이 가짜 데이터도 넣을 수 있습니다.
-- INSERT INTO coupon_issuances (user_id, coupon_id, coupon_code, status, issued_at, expires_at) VALUES
-- (1, (SELECT id FROM coupons WHERE name = '골든 패티 쿠폰'), 'GP_12345', 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY));