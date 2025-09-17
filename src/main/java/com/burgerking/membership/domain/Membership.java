package com.burgerking.membership.domain;

import com.burgerking.membership.domain.enums.MembershipGrade;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;


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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일

    @Builder
    public Membership(Long userId, MembershipGrade grade) {
        this.userId = userId;
        this.grade = grade;
        
        LocalDateTime now = LocalDateTime.now();
       
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

        this.updatedAt = evaluationTime;

        return isGradeChanged;
    }
}