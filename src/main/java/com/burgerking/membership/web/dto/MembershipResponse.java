package com.burgerking.membership.web.dto;


import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.enums.MembershipGrade;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MembershipResponse {
    /**
     * 멤버십 ID
     */
    private Long id;

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 멤버십 등급
     */
    private MembershipGrade grade;

    /**
     * 마지막 등급 평가일
     */
    private LocalDateTime lastEvaluationDate;

    /**
     * 다음 등급 평가 예정일
     */
    private LocalDateTime nextEvaluationDate;

    /**
     * 생성일
     */
    private LocalDateTime createdAt;

    /**
     * 수정일
     */
    private LocalDateTime updatedAt;

    public static MembershipResponse from(Membership membership) {
        return MembershipResponse.builder()
            .id(membership.getId())
            .userId(membership.getUserId())
            .grade(membership.getGrade())
            .lastEvaluationDate(membership.getLastEvaluationDate())
            .nextEvaluationDate(membership.getNextEvaluationDate())
            .createdAt(membership.getCreatedAt())
            .updatedAt(membership.getUpdatedAt())
            .build();
    }
}
