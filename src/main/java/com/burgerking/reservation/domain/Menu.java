package com.burgerking.reservation.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 
 * 메뉴 관련 도메인 데이터
 * 
 * Explain
 * 특정 메뉴의 기본 정보
 * OrderItem | Order -> 다대일 관계, Menu Table 또한 동일한 관계
 * 
 * Field
 * ID / 메뉴명 / 가격 / 메뉴를 취급하는 매장 / 판매 가능 여부 
 * 메뉴 데이터 생성 시각 / 메뉴 데이터 수정 시각
 * 
 * Method
 * 메뉴 데이터 생성 시각 갱신 / 메뉴 데이터 수정 시각 갱신
*/
@Entity
@Table(name = "menus")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu { 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;       
    
    @Column(nullable = false)
    private BigDecimal price; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;        
    
    @Column(nullable = false)
    private boolean available;  
    
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