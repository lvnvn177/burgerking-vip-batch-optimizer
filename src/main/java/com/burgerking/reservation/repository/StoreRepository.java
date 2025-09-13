package com.burgerking.reservation.repository;

import com.burgerking.reservation.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 
 * 매장 관련 Repository
 * 
 * 조회
 * 오픈한 매장 리스트
*/
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    
    List<Store> findByIsOpenTrue();
}
