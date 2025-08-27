package com.burgerking.membership.vip_batch_optimizer.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burgerking.membership.vip_batch_optimizer.repository.MemberRepository;
import com.burgerking.membership.vip_batch_optimizer.service.MembershipBatchServiceNonOptimized;
import com.burgerking.membership.vip_batch_optimizer.domain.Member;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BatchController {
    
    private final MembershipBatchServiceNonOptimized batchServiceNonOptimized;
    private final MemberRepository memberRepository;

    @PostMapping("/batch/run-non-optimized")
    public ResponseEntity<Map<String, Object>> runNonOptimizedBatch() {
        long startTime = System.currentTimeMillis();

        batchServiceNonOptimized.updateMembershipLevels();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("executionTimeMs", executionTime);

        return ResponseEntity.ok(response);
    }

    // API 부하 테스트를 위한 엔드포인트
    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberRepository.findAll());
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
