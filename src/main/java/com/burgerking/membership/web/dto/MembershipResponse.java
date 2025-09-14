package com.burgerking.membership.web.dto;

import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.domain.enums.MembershipGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "멤버십 응답 DTO")
public class MembershipResponse {
    @Schema(description = "멤버십 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "멤버십 등급", example = "NORMAL")
    private MembershipGrade grade;

    @Schema(description = "마지막 등급 평가일", example = "2023-01-01T00:00:00")
    private LocalDateTime lastEvaluationDate;

    @Schema(description = "다음 등급 평가 예정일", example = "2024-01-01T00:00:00")
    private LocalDateTime nextEvaluationDate;

    @Schema(description = "생성일", example = "2022-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일", example = "2023-01-01T00:00:00")
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
