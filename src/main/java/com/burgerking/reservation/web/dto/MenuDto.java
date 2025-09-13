package com.burgerking.reservation.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 
 * 메뉴 관련 Dto
 * 
 * Field
 * 메뉴 ID / 메뉴 이름 / 메뉴 가격 / 해당 메뉴를 취급하는 매장 ID / 주문 가능 여부 
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {
    private Long id;
    private String name;   
    private BigDecimal price;
    private Long storeId;
    private boolean available; 
}