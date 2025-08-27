package com.burgerking.membership.vip_batch_optimizer.util;

import com.burgerking.membership.vip_batch_optimizer.domain.Member;
import com.burgerking.membership.vip_batch_optimizer.domain.Order;
import com.burgerking.membership.vip_batch_optimizer.domain.Member.MembershipLevel;
import com.burgerking.membership.vip_batch_optimizer.domain.Order.OrderStatus;
import com.burgerking.membership.vip_batch_optimizer.repository.MemberRepository;
import com.burgerking.membership.vip_batch_optimizer.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
@Component
@RequiredArgsConstructor
public class DummyDataGenerator {
    
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final Random random = new Random();

    // 애플리케이션 시작 시 자동 실행되도록 설정 (dev 프로필에서만)
    @Bean
    @Profile("dev")
    public CommandLineRunner initDatabase() {
        return args -> {
            if (memberRepository.count() > 0) {
                System.out.println("데이터베이스에 이미 데이터가 있습니다. 더미 데이터 생성을 건너뜁니다.");
                return;
            }
        };
    }

    /**
     * 수동으로 호출할 수 있는 데이터 생성 메서드
     */
    @Transactional
    public void generateData(int memberCount, int orderCount) {
        System.out.println("더미 데이터 생성 시작: " + memberCount + "명의 회원" + orderCount + "건의 주문");
        long startTime = System.currentTimeMillis();

        // 1. 회원 생성
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < memberCount; i++) {
            Member member = new Member();
            member.setName("User" + i);
            member.setEmail("user" + i + "@example.com");
            member.setPhone("010-" + (1000 + i) + "-" + (2000 + i));

            // 회원 등급 랜덤 설정
            int levelRandom = random.nextInt(100);
            if (levelRandom < 70) {
                member.setMembershipLevel(MembershipLevel.REGULAR); // 70%
            } else if (levelRandom < 85) {
                member.setMembershipLevel(MembershipLevel.GOLD); // 15%
            } else if (levelRandom < 95) {
                member.setMembershipLevel(MembershipLevel.PLATINUM); // 10%
            } else {
                member.setMembershipLevel(MembershipLevel.VIP); // 5%
            }

            member.setLastLevelUpdatedDate(LocalDateTime.now().minusMonths(1));
            members.add(member);

            // 1,000명 단위로 저장 (메모리 효율)
            if (members.size() >= 1000 || i == memberCount - 1) {
                memberRepository.saveAll(members);
                members.clear();
                System.out.println("회원" + (i + 1) + "명 생성 완료");
            }
        }

        // 2. 주문 생성 
        LocalDateTime now  = LocalDateTime.now();
       
        List<Order> orders = new ArrayList<>();
        List<Long> memberIds = memberRepository.findAllMemberIds();
        int memberSize = memberIds.size();

        for (int i = 0; i < orderCount; i++) {
            Order order = new Order();

            // 랜덤 회원 선택
            Long memberId = memberIds.get(random.nextInt(memberSize));
            Member member = new Member();
            member.setId(memberId);
            order.setMember(member);

            // 주문 금액 설정 (5천원 ~ 30만원)
            BigDecimal amount = new BigDecimal(5000 + random.nextInt(295001));
            order.setAmount(amount);

            // 주문 날짜 설정 (최근 3개월 내)
            long randomDays = random.nextInt(90); // 0~89일 전 
            long randomHours = random.nextInt(24); // 0~23시간 전
            LocalDateTime orderDate = now.minusDays(randomDays).minusHours(randomHours);
            order.setOrderDate(orderDate);

    
            // 주문 상태 설정 (95%는 완료, 5%는 취소)
            if (random.nextInt(100) < 95) {
                order.setStatus(OrderStatus.COMPLETED);
            } else {
                order.setStatus(OrderStatus.CANCELLED);
            }

            orders.add(order);

            // 1,000건 단위로 저장 (메모리 효율)
            if (orders.size() >= 1000 || i == orderCount - 1) {
                orderRepository.saveAll(orders);
                orders.clear();
                System.out.println("주문" + (i + 1) + "건 생성 완료");
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("더미 데이터 생성 완료! 소요 시간: " + (endTime - startTime) / 1000 + "초");

    }

     /**
     * 대규모 데이터 생성을 위한 메서드 (수백만 건)
     */

     @Transactional
     public void generateMassiveData(int memberCount, int orderCount) {
        // 회원 생성 시작 
        System.out.println("대규모 더미 데이터 생성: " + memberCount + "명의 회원, " + orderCount + "건의 주문");
        long startTime = System.currentTimeMillis();

        // 기본 생성 메소드 활용, 더 큰 단위로 처리
        int batchSize = 10000;
        for (int i = 0; i < memberCount; i += batchSize) {
            int currentBatchSize = Math.min(batchSize, memberCount - i);
            generateData(currentBatchSize, 0); // 회원만 생성
            System.out.println("회원 " + (i + currentBatchSize) + "/" + memberCount + " 생성 완료");
        }

        for (int i = 0; i < orderCount; i += batchSize) {
            int currentBatchSize = Math.min(batchSize, orderCount - i);

            List<Order> orders = new ArrayList<>();
            List<Long> memberIds = memberRepository.findAllMemberIds();
            int memberSize = memberIds.size();
            
            for (int j = 0; j < currentBatchSize; j++) {
                Order order = new Order();
                
                // 랜덤 회원 선택
                Long memberId = memberIds.get(random.nextInt(memberSize));
                Member member = new Member();
                member.setId(memberId);
                order.setMember(member);

                // 주문 금액 설정 (5천원 ~ 30만원)
                BigDecimal amount = new BigDecimal(5000 + random.nextInt(295001));
                order.setAmount(amount);

                // 주문 날짜 설정 (최근 3개월 내)
                long randomDays = random.nextInt(90); // 0~89일 전
                long randomHours = random.nextInt(24);  // 0~23시간 전
                LocalDateTime orderDate = LocalDateTime.now().minusDays(randomDays).minusHours(randomHours);
                order.setOrderDate(orderDate);

                // 주문 상태 설정 (95%는 완료, 5%는 취소)
                if (random.nextInt(100) < 95) {
                    order.setStatus(OrderStatus.COMPLETED);
                } else {
                    order.setStatus(OrderStatus.CANCELLED);
                }

                orders.add(order);

                // 1,000건 단위로 저장 (메모리 효율)
                if (orders.size() >= 1000 || j == currentBatchSize - 1) {
                    orderRepository.saveAll(orders);
                    orders.clear();
                }

                System.out.println("주문" + (i + currentBatchSize) + "/" + orderCount + "생성 완료");

            }

            long endTime = System.currentTimeMillis();
            System.out.println("대규모 더미 데이터 생성 완료! 소요 시간: " + (endTime - startTime) / 1000 + "초");
        }
     }
}
