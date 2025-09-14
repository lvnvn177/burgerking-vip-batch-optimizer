package com.burgerking.reservation.web;

import com.burgerking.reservation.service.OrderService;
import com.burgerking.reservation.web.dto.OrderRequest;
import com.burgerking.reservation.web.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation/reservations")
public class ReservationController {

    private final OrderService orderService;

    /**
     * (테스트용) 비최적화된 상품 예약 엔드포인트.
     * 동시성 문제를 시뮬레이션합니다.
     * POST /api/reservations/product-non-optimized/{productId}
     */
    @PostMapping("/product-non-optimized/{productId}")
    public ResponseEntity<OrderResponse> reserveProductNonOptimized(@PathVariable Long productId, @RequestBody @Valid OrderRequest request) {
        OrderResponse response = orderService.createOrderNonOptimized(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * (테스트용) 최적화된 상품 예약 엔드포인트.
     * 비관적 락킹을 사용하여 동시성 문제를 처리합니다.
     * POST /api/reservations/product/{productId}
     */
    @PostMapping("/product/{productId}")
    public ResponseEntity<OrderResponse> reserveProduct(@PathVariable Long productId, @RequestBody @Valid OrderRequest request) {
        OrderResponse response = orderService.createOrderOptimized(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}