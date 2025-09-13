package com.burgerking.reservation.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/** 
 * 매장 관련 Dto
 * 
 * Field
 * 매장 ID / 오픈 여부
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto { 
    private Long id;
    private boolean isOpen;
}