package com.burgerking.coupon.repository;

import com.burgerking.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
  
   // CouponRepository 인터페이스에서
    List<Coupon> findByName(String name);
}
