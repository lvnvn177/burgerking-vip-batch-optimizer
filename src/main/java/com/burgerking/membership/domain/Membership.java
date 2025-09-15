package com.burgerking.membership.domain;

import com.burgerking.membership.domain.enums.MembershipGrade;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 멤버십 ID

    @Column(name = "user_id", nullable = false)
    private Long userId;        // 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private MembershipGrade grade; // 멤버십 등급

    @Column(name = "last_evaluation_date", nullable = false)
    private LocalDateTime lastEvaluationDate; // 마지막 등급 평가일

    @Column(name = "next_evaluation_date", nullable = false)
    private LocalDateTime nextEvaluationDate; // 다음 등급 평가 예정일 (매월 1일 09:00 AM)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일

    @Builder
    public Membership(Long userId, MembershipGrade grade) {
        this.userId = userId;
        this.grade = grade;
        
        LocalDateTime now = LocalDateTime.now();
        this.lastEvaluationDate = now;
        
        // 초기 설정: 다음 평가일은 다음 달 1일 오전 9시
        this.nextEvaluationDate = now.plusMonths(1)
                .withDayOfMonth(1)
                .withHour(9)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 멤버십 등급을 갱신합니다.
     * @param newGrade 새롭게 계산된 등급
     * @param evaluationTime 등급 평가가 진행된 시점
     * @return 등급이 변경되었는지 여부
     */
    public boolean updateGrade(MembershipGrade newGrade, LocalDateTime evaluationTime) {
        boolean isGradeChanged = !this.grade.equals(newGrade);
        
        if (isGradeChanged) {
            this.grade = newGrade;
        }
        
        this.lastEvaluationDate = evaluationTime;
        this.nextEvaluationDate = evaluationTime.plusMonths(1)
                .withDayOfMonth(1)
                .withHour(9)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        this.updatedAt = evaluationTime;

        return isGradeChanged;
    }
}