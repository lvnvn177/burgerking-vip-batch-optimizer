package com.burgerking.reservation.domain.enums;

/** 
 * 주문 항목 관련 도메인 데이터
 * 
 * Explain
 * 주문의 상태 타입
 * 
 * Field
 * 대기중 / 확인됨 / 준비중 / 준비완료 / 완료 / 취소됨
*/
public enum OrderStatus {
    PENDING,    
    CONFIRMED,  
    PREPARING,  
    READY,      
    COMPLETED, 
    CANCELED    
}