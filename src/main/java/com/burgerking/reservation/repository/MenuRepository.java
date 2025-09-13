package com.burgerking.reservation.repository;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
