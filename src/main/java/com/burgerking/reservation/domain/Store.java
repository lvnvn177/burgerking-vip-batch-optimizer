package com.burgerking.reservation.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 
 * 매장 관련 도메인 데이터
 * 
 * Field
 * ID / 오픈 여부 / 데이터 생성 시각 / 가장 최근 수정 시각 
 * 
 * Method
 * 생성 시각 갱신 / 최신 수정 시각 갱신 
*/
@Entity
@Table(name = "stores")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name; // 매장 이름 추가
    
    @Column(nullable = false)
    private boolean isOpen;
    
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