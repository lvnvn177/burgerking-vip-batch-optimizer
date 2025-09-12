package com.burgerking.reservation.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

import com.burgerking.reservation.domain.enums.ReservationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations") // 테이블 명은 '예약 내역'으로 명명했습니다.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예약의 고유 ID

    @Column(nullable = false)
    private Long userId; // 예약한 사용자의 ID (외부 시스템의 사용자 ID를 참조)

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계: 여러 예약이 하나의 예약 가능 항목을 참조할 수 있습니다.
    @JoinColumn(name = "reservable_item_id", nullable = false)
    private ReservableItem reservableItem; // 어떤 항목을 예약했는지

    @Column(nullable = false)
    private LocalDate reservationDate; // 예약 날짜

    @Column(nullable = false)
    private LocalTime startTime; // 예약 시작 시간

    @Column(nullable = false)
    private LocalTime endTime; // 예약 종료 시간

    @Column(nullable = false)
    private Integer numberOfPeople; // 예약 인원

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // DB에 문자열로 저장
    private ReservationStatus status;  // 예약 상태 (예: "PENDING", "CONFIRMED", "CANCELED", "COMPLETED", "NO_SHOW") - enum으로 관리할 수도 있습니다.

    @Column(length = 500)
    private String specialRequests; // 특별 요청 사항

    @Column(nullable = false)
    private LocalDateTime createdAt; // 예약 생성 일시

    @Column
    private LocalDateTime updatedAt; // 마지막 업데이트 일시

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}