package com.burgerking.coupon.repository;

import com.burgerking.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCouponCode(String couponCode);

    // CouponRepository 인터페이스에서
    List<Coupon> findByName(String name);
}
