package com.burgerking.membership.vip_batch_optimizer.web;

import com.burgerking.membership.vip_batch_optimizer.util.DummyDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dummy")
@RequiredArgsConstructor
public class DummyController {
    
    private final DummyDataGenerator dummyDataGenerator;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateDummyData(
        @RequestParam(defaultValue = "1000") int memberCount,
        @RequestParam(defaultValue = "10000") int orderCount)  {
        
        long startTime = System.currentTimeMillis();

        dummyDataGenerator.generateData(memberCount, orderCount);

        long endTiem = System.currentTimeMillis();
        long executionTime = endTiem  - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("memberCount", memberCount);
        response.put("orderCount", orderCount);
        response.put("executionTimeMs", executionTime);

        return ResponseEntity.ok(response);
    }
}
