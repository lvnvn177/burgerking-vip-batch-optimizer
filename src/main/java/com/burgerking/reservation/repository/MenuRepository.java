package com.burgerking.reservation.repository;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/** 
 * 메뉴 관련 Repository
 * 
 * 조회
 * 매장별 / 매장별 판매 가능한 메뉴 / 메뉴 이름 
*/
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    // 매장별 메뉴 찾기
    List<Menu> findByStore(Store store);

    // 매장별 판매 가능한 메뉴 찾기
    List<Menu> findByStoreAndAvailableTrue(Store store);

    // 이름으로 메뉴 찾기
    List<Menu> findByNameContaining(String name);

    // 메뉴 ID로 메뉴를 찾고 비관적 락을 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Menu m where m.id = :id")
    Optional<Menu> findByIdWithPessimisticLock(@Param("id") Long id);
}
