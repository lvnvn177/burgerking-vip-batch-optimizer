-- 외래 키 제약조건이 있을 경우 사용하는 방법
SET FOREIGN_KEY_CHECKS = 0; -- 외래 키 체크 비활성화

TRUNCATE TABLE members;
TRUNCATE TABLE orders;
TRUNCATE TABLE sum_orders;

SET FOREIGN_KEY_CHECKS = 1; -- 외래 키 체크 다시 활성화