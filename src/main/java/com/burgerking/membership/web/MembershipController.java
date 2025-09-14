package com.burgerking.membership.web;

import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.service.MembershipService;
import com.burgerking.membership.web.dto.MembershipResponse;
import com.burgerking.membership.web.dto.OrderProcessRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Membership API", description = "멤버십 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memberships")
public class MembershipController {
    
    private final MembershipService membershipService;

    /**
     * 특정 사용자의 멤버십 정보를 조회합니다.
     * 사용자의 멤버십 정보가 없으면 새로 생성하여 반환합니다.
     * GET /api/memberships/{userId}
     */
    @Operation(summary = "특정 사용자의 멤버십 정보 조회 또는 생성", description = "userId에 해당하는 멤버십 정보를 조회합니다. 존재하지 않으면 새로 생성하여 반환합니다.")
    @ApiResponse(responseCode = "200", description = "멤버십 정보 조회 또는 생성 성공")
    @Parameter(name = "userId", description = "사용자 ID", required = true, example = "1")
    @GetMapping("/{userId}")
    public ResponseEntity<MembershipResponse> getOrCreateMembership(@PathVariable Long userId) {
        Membership membership = membershipService.createOrGetMembership(userId);
        return ResponseEntity.ok(MembershipResponse.from(membership));
    }

    /**
     * 새로운 주문 정보를 처리하여 멤버십 관련 데이터를 업데이트합니다.
     * POST /api/memberships/orders
     */
    @Operation(summary = "새로운 주문 처리", description = "새로운 주문 정보를 처리하여 멤버십 관련 데이터를 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "주문 처리 및 멤버십 데이터 업데이트 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @PostMapping("/orders")
    public ResponseEntity<String> processOrder (@Valid @RequestBody OrderProcessRequest request) {
        membershipService.processNewOrder(request.getUserId(), request.getOrderNumber().toString(), request.getOrderAmount());
        return ResponseEntity.status(HttpStatus.OK).body("Order processed successfully and membership data updated.");
    }

    /**
     * (관리자용 또는 테스트용) 모든 사용자의 멤버십 등급을 수동으로 갱신합니다.
     * 실제 운영에서는 스케줄러가 이 역할을 수행합니다.
     * POST /api/memberships/evaluate
     */
    @Operation(summary = "(관리자/테스트용) 모든 사용자의 멤버십 등급 수동 갱신", description = "모든 사용자의 멤버십 등급을 수동으로 갱신합니다. 실제 운영에서는 스케줄러가 수행합니다.")
    @ApiResponse(responseCode = "200", description = "모든 멤버십 등급 평가 및 갱신 성공")
    @PostMapping("/evaluate")
    public ResponseEntity<String> evaluateAndRenewAllMembershipGrades() {
        membershipService.evaluateAndRenewAllMembershipGrades();
        return ResponseEntity.status(HttpStatus.OK).body("All membership grades evaluated and renewed.");
    }
    /**
     * (테스트용) 비최적화된 멤버십 등급 조정 배치를 실행합니다.
     * POST /api/memberships/adjust-batch-non-optimized
     */
    @Operation(summary = "(테스트용) 비최적화된 멤버십 등급 조정 배치 실행", description = "비최적화된 방식으로 멤버십 등급 조정 배치를 실행합니다.")
    @ApiResponse(responseCode = "200", description = "비최적화된 배치 작업 시작")
    @PostMapping("/adjust-batch-non-optimized")
    public ResponseEntity<String> runNonOptimizedBatch() {
        membershipService.runNonOptimizedBatch();
        return ResponseEntity.ok("Non-optimized batch job started.");
    }

    /**
     * (테스트용) 최적화된 멤버십 등급 조정 배치를 실행합니다.
     * POST /api/memberships/adjust-batch
     */
    @Operation(summary = "(테스트용) 최적화된 멤버십 등급 조정 배치 실행", description = "최적화된 방식으로 멤버십 등급 조정 배치를 실행합니다.")
    @ApiResponse(responseCode = "200", description = "최적화된 배치 작업 시작")
    @PostMapping("/adjust-batch")
    public ResponseEntity<String> runOptimizedBatch() {
        membershipService.runOptimizedBatch();
        return ResponseEntity.ok("Optimized batch job started.");
    }
}
