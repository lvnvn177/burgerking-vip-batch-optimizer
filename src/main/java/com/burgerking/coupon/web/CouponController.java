package com.burgerking.coupon.web;

import com.burgerking.coupon.service.CouponService;
import com.burgerking.coupon.web.dto.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    
    private final CouponService couponService;

    /**
     * 새로운 쿠폰 생성 (관리자용)
     */
    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest couponRequest) {
        log.info("쿠폰 생성 요청: {}", couponRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(couponService.createCoupon(couponRequest));
    }

    /**
     * 쿠폰 정보 조회
     */
    @GetMapping("/{couponID}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable Long couponId) {
        log.info("쿠폰 조회 요청: id={}", couponId);
        return couponService.getCouponById(couponId)
            .map(ResponseEntity::ok) 
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다: " + couponId));  
    }

    /**
     * 쿠폰 발급 (기본)
     */
    @PostMapping("/issue")
    public ResponseEntity<CouponResponse> issueCoupon(@Valid @RequestBody CouponIssueRequest request) {
        log.info("쿠폰 발급 요청: {}", request);
        try {
            CouponResponse response = couponService.issueCoupon(request.getCouponId(), request.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        
    }

    /**
     * 쿠폰 발급 (비관적 락 사용)
     */
    @PostMapping("/issue/pessimistic")
    public ResponseEntity<CouponResponse> issueCouponWithPessimisticLock(@Valid @RequestBody CouponIssueRequest request) {
        log.info("쿠폰 발급 요청 (비관적 락): {}", request);
        try {
            CouponResponse response = couponService.issueCouponWithPessimisticLock(
                request.getCouponId(), request.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

     /**
     * 쿠폰 발급 (낙관적 락 사용)
     */
    @PostMapping("/issue/optimistic")
    public ResponseEntity<CouponResponse> issueCouponWithOptimisticLock(@Valid @RequestBody CouponIssueRequest request) {
        log.info("쿠폰 발급 요청 (낙관적 락): {}", request);
        try {
            CouponResponse response = couponService.issueCouponWithOptimisticLock(
                request.getCouponId(), request.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 쿠폰 발급 (원자적 연산 사용)
     */
    @PostMapping("/issue/atomic")
    public ResponseEntity<CouponResponse> issueCouponWithAtomicOperation(@Valid @RequestBody CouponIssueRequest request) {
        log.info("쿠폰 발급 요청 (원자적 연산): {}", request);
        try {
            CouponResponse response = couponService.issueCouponWithAtomicOperation(
                request.getCouponId(), request.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 쿠폰 발급 (Redis 분산 락 사용)
     */
    @PostMapping("/issue/redis")
    public ResponseEntity<CouponResponse> issueCouponWithRedisLock(@Valid @RequestBody CouponIssueRequest request) {
        log.info("쿠폰 발급 요청 (Reids 락): {}", request);
        try {
            CouponResponse response = couponService.issueCouponWithRedisLock(
                request.getCouponId(), request.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 쿠폰 사용
     */
    @PostMapping("/use")
    public ResponseEntity<Void> useCoupon(@Valid @RequestBody CouponUseRequest request) {
        log.info("쿠폰 사용 요청: {}", request);
        boolean result = couponService.useCoupon(request.getCouponCode(), request.getOrderReference());
        if(result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 쿠폰 사용 취소
     */
    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelCoupon(@RequestParam String couponCode) {
        log.info("쿠폰 사용 취소 요청: code={}", couponCode);
        boolean result = couponService.cancelCoupon(couponCode);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 사용자의 쿠폰 목록 조회
     */
    @GetMapping(".user/{userId}")
    public ResponseEntity<List<CouponResponse>> getUserCoupons(@PathVariable Long userId) {
        log.info("사용자 쿠폰 목록 조회: userId={}", userId);
        List<CouponResponse> coupons = couponService.getUserCoupons(userId);
        return ResponseEntity.ok(coupons);
    }

    /**
     * 쿠폰 재고 확인
     */
    @GetMapping("/{couponId}/stock")
    public ResponseEntity<Integer> getCouponStock(@PathVariable Long couponId) {
        log.info("쿠폰 재고 확인: couponId={}", couponId);
        try {
            int remainingQuantity = couponService.getRemainingQuantity(couponId);
            return ResponseEntity.ok(remainingQuantity);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

     /**
     * 에러 처리를 위한 전역 예외 핸들러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (e instanceof ResponseStatusException) {
            status = HttpStatus.valueOf(((ResponseStatusException) e).getStatusCode().value());
        } else if (e instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (e instanceof IllegalStateException) {
            status = HttpStatus.BAD_REQUEST;
        }
        
        ErrorResponse errorResponse = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        
        log.error("API 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
