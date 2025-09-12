package com.burgerking.membership.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
// import java.time.YearMonth; // 이 import는 MonthlyOrder에서 사용될 예정

import com.burgerking.membership.domain.enums.MembershipGrade;

@Entity
@Table(name = "memberships")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipGrade grade;
    
    @Column(nullable = false)
    private Integer totalPoints;

    @Column(nullable = false)
    private Integer availablePoints;
    
    // 마지막 등급 평가일 (이 정보는 주로 스케줄러가 다음 평가 주기 관리하는 데 사용될 수 있습니다)
    @Column(nullable = false)
    private LocalDateTime lastEvaluationDate;
    
    // 다음 등급 평가 예정일 (이 날짜에 스케줄러가 실행되어야 함)
    @Column(nullable = false)
    private LocalDateTime nextEvaluationDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Membership(Long userId, MembershipGrade grade) {
        this.userId = userId;
        this.grade = grade;
        this.totalPoints = 0;
        this.availablePoints = 0;
        
        LocalDateTime now = LocalDateTime.now();
        this.lastEvaluationDate = now;
        
        // 다음 평가일은 다음 달 1일 오전 9시
        this.nextEvaluationDate = now.plusMonths(1)
                .withDayOfMonth(1)
                .withHour(9)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        
        this.createdAt = now;
        this.updatedAt = now;
    }

    // 등급 갱신 (스케줄러에 의해 새로운 등급이 계산되어 적용)
    public void updateGrade(MembershipGrade newGrade, LocalDateTime evaluationTime) {
        if (!this.grade.equals(newGrade)) {
            this.grade = newGrade;
            this.updatedAt = evaluationTime;
        }
        // 평가 시간 업데이트 및 다음 평가 예정일 설정 (이 부분은 스케줄러가 매월 호출 시점에 갱신할 내용입니다)
        this.lastEvaluationDate = evaluationTime;
        this.nextEvaluationDate = evaluationTime.plusMonths(1)
                .withDayOfMonth(1)
                .withHour(9)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        this.updatedAt = evaluationTime; // 업데이트 시점 기록
    }
    
    // 포인트 적립
    public void addPoints(int points) {
        this.totalPoints += points;
        this.availablePoints += points;
        this.updatedAt = LocalDateTime.now(); // 포인트 변경 시에도 updated_at 갱신
    }

    // 포인트 사용
    public boolean usePoints(int points) {
        if (this.availablePoints < points) {
            return false;
        }
        this.availablePoints -= points;
        this.updatedAt = LocalDateTime.now(); // 포인트 변경 시에도 updated_at 갱신
        return true;
    }
}