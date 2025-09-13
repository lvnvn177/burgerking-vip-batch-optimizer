package com.burgerking.reservation.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;

/** 
 * 주문 항목 관련 도메인 데이터
 * 
 * Explain
 * 고객이 한번의 주문에서 요청하는 여러 항목
 * OrderItem | Order -> 다대일 관계, Menu Table 또한 동일한 관계
 * 
 * Field
 * ID / 해당 주문 항목을 포함하는 주문 / 주문 항목에 포함된 메뉴 / 메뉴의 수량 / 개당 가격 
*/
@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {  
    @Id                 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;        
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;         
    
    @Column(nullable = false)
    private int quantity;       
    
    @Column(nullable = false)
    private BigDecimal price;  
}
