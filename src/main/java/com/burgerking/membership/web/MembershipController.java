package com.burgerking.membership.web;

import com.burgerking.membership.domain.Membership;
import com.burgerking.membership.service.MembershipService;
import com.burgerking.membership.web.dto.MembershipResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Membership API", description = "멤버십 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership")
public class MembershipController {
    
    private final MembershipService membershipService;

    /**
     * 특정 사용자의 멤버십 정보를 조회합니다.
     * 사용자의 멤버십 정보가 없으면 새로 생성하여 반환합니다.
     * GET /api/membership/{userId}
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
     * (테스트용) 비최적화된 멤버십 등급 조정 배치를 실행합니다.
     * POST /api/membership/adjust-batch-non-optimized
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
     * POST /api/membership/adjust-batch-optimized
     */
    @Operation(summary = "(테스트용) 최적화된 멤버십 등급 조정 배치 실행", description = "최적화된 방식으로 멤버십 등급 조정 배치를 실행합니다.")
    @ApiResponse(responseCode = "200", description = "최적화된 배치 작업 시작")
    @PostMapping("/adjust-batch-optimized")
    public ResponseEntity<String> runOptimizedBatch() {
        membershipService.runOptimizedBatch();
        return ResponseEntity.ok("Optimized batch job started.");
    }
    /**
     * (테스트용) 고객 및 주문 더미 데이터를 생성합니다.
     * POST /api/membership/generate-test-data
     */
    @Operation(summary = "(테스트용) 고객 및 주문 더미 데이터 생성", description = "성능 테스트를 위한 고객 및 주문 더미 데이터를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "더미 데이터 생성 성공")
    @PostMapping("/generate-test-data")
    public ResponseEntity<String> generateTestData(
        @Parameter(name = "numberOfMembers", description = "생성할 고객 수", required = true, example = "100")
        @RequestParam int numberOfMembers,
        @Parameter(name = "maxOrdersPerMember", description = "한 고객당 최대 주문 수", required = true, example = "50")
        @RequestParam int maxOrdersPerMember
    ) {
        membershipService.generateTestData(numberOfMembers, maxOrdersPerMember);
        return ResponseEntity.status(HttpStatus.OK).body(numberOfMembers + "명의 고객과 최대 " + maxOrdersPerMember + "건의 주문 더미 데이터가 생성되었습니다.");
    }
    /**
     * 신규 주문을 처리합니다.
     * POST /api/membership/orders
     */
    @Operation(summary = "신규 주문 처리", description = "신규 주문 정보를 받아 처리하고, 멤버십 등급에 반영합니다.")
    @ApiResponse(responseCode = "200", description = "주문 처리 성공")
    @PostMapping("/orders")
    public ResponseEntity<Void> processOrder(@RequestBody com.burgerking.membership.web.dto.OrderProcessRequest request) {
        membershipService.processOrder(request);
        return ResponseEntity.ok().build();
    }
}
