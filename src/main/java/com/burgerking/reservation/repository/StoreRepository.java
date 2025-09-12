package com.burgerking.reservation.repository;

import com.burgerking.reservation.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    // 영업 중인 매장 찾기
    List<Store> findByIsOpenTrue();
}
