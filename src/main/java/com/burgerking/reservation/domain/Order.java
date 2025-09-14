package com.burgerking.reservation.domain;

import com.burgerking.reservation.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;



/** 
 * 주문 관련 도메인 데이터
 * 
 * Explain
 * 고객이 요청한 주문의 명세서
 * OrderItem | Order -> 다대일 관계, Menu Table 또한 동일한 관계
 * 
 * Field
 * ID / 주문을 요청한 고객 ID / 주문을 요청 받은 매장 ID / 주문 픽업 시간 
 * 총 주문 금액 / 현재 주문 상태 / 주문 요청 시각 / 주문이 수정된 시각
 * 
 * Method
 * 주문 생성 시각 갱신 / 주문 수정 시각 갱신
*/
@Entity(name = "ReservationOrder")
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;       
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;       
    
    @Column(nullable = false, unique = true)
    private String orderNumber;
    
    @Column(nullable = false)
    private LocalDateTime pickupTime;
    
    @Column(nullable = false)
    private BigDecimal totalAmount; 
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; 
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}