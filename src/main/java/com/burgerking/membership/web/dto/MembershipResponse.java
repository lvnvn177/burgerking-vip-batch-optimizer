package com.burgerking.membership.web.dto;


import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.enums.MembershipGrade;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MembershipResponse {
    private Long id;
    private Long userId;
    private MembershipGrade grade;
    private LocalDateTime lastEvaluationDate;
    private LocalDateTime nextEvaluationDate;
    private LocalDateTime createdAt;
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
