package com.burgerking.membership.vip_batch_optimizer.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HomeController {
    
    @GetMapping("/")
    @ResponseBody
    public String home() {
         return "버거킹 VIP 멤버십 배치 성능 최적화 프로젝트가 실행 중입니다.<br>" +
               "다음 API 엔드포인트를 사용해보세요:<br>" +
               "- POST /api/dummy/generate - 더미 데이터 생성<br>" +
               "- POST /api/batch/run-non-optimized - 비최적화 배치 실행<br>" +
               "- GET /api/members - 모든 회원 조회";
    }
}
