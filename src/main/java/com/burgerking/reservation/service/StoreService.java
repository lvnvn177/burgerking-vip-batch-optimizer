package com.burgerking.reservation.service;

import com.burgerking.reservation.web.dto.StoreDto;

import java.util.List;

/** 
 * 매장 관련 인터페이스 
 * 
 * 조회
 * 매장 ID / 모든 매장 / 오픈 여부
 * 
 * 생성 
 * 매장 Dto 
 * 
 * 수정
 * 수정하고자 하는 매장 ID 및 수정된 매장 Dto
 * 
 * 삭제
 * 매장 ID
*/
public interface StoreService {
    StoreDto getStoreById(Long id); 
    List<StoreDto> getAllStores();  
    List<StoreDto> getOpenStores(); 
    StoreDto createStore(StoreDto storeDto);   
    StoreDto updateStore(Long id, StoreDto storeDto);   
    void deleteStore(Long id);  
}