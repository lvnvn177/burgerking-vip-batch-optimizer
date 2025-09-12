package com.burgerking.coupon.repository;

import com.burgerking.coupon.domain.CouponStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {
    
    Optional<CouponStock> findByCoupon_CouponCode(String couponCode);

    /**
     * 비관적 락(PESSIMISTIC_WRITE)을 걸고 CouponStock 엔티티를 조회합니다.
     * 이 쿼리는 데이터베이스에서 해당 레코드에 대해 쓰기 락을 걸어 다른 트랜잭션의 접근을 막습니다.
     * 이 메서드는 반드시 @Transactional 메서드 내에서 호출되어야 락이 유효합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cs FROM CouponStock cs WHERE cs.coupon.couponCode = :couponCode")
    Optional<CouponStock> findByCouponCodeWithPessimisticLock(@Param("couponCode") String couponCode);

    /**
     * CouponStock의 remainingQuantity를 원자적으로 1 감소시킵니다.
     * 이 쿼리는 JDBC 레벨에서 바로 실행되어 데이터베이스의 락 메커니즘을 사용하므로,
     * 애플리케이션 레벨의 락(비관적/낙관적 락)과 달리 별도의 락 로직 없이도 경쟁 상태를 방지할 수 있습니다.
     * WHERE 절에 remainingQuantity > 0 조건을 추가하여 음수가 되는 것을 방지합니다.
     *
     * @Modifying 어노테이션은 INSERT, UPDATE, DELETE 쿼리에 사용됩니다.
     * 이 메서드는 트랜잭션 내에서 호출되어야 하며, 호출 후 영속성 컨텍스트를 동기화해야 할 수 있습니다.
     * (예: EntityManager.clear() 또는 엔티티 재조회)
     * return 변경된 행의 수 (0 또는 1)
     */
    @Modifying
    @Query("UPDATE CouponStock cs SET cs.remainingQuantity = cs.remainingQuantity - 1 WHERE cs.coupon.couponCode = :couponCode AND cs.remainingQuantity > 0")
    int decreaseStockAtomic(@Param("couponCode") String couponCode);

    // 낙관적 락은 엔티티의 @Version 필드를 통해 JPA가 자동으로 처리
    // findById(id) 후 save() 할 때 버전 불일치 시 OptimisticLockException 발생.

}
