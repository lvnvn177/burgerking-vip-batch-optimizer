package com.burgerking.membership.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isActive;

    @Builder
    public Membership(Long userId, MembershipGrade grade, Integer totalPoints, Integer availablePoints) {
        this.userId = userId;
        this.grade = grade;
        this.totalPoints = totalPoints;
        this.availablePoints = availablePoints;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }

    public void updateGrade(MembershipGrade grade) {
        this.grade = grade;
        this.updatedAt = LocalDateTime.now();
    }

    public void addPoints(int points) {
        this.totalPoints += points;
        this.availablePoints += points;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean usePoints(int points) {
        if (this.availablePoints < points) {
            return false;
        }
        this.availablePoints -= points;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
}