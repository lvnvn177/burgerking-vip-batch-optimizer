package com.burgerking.reservation.service;

import com.burgerking.reservation.web.dto.MenuDto;

import java.util.List;

/** 
 * 메뉴 관련 인터페이스
 * 
 * 조회
 * 전체 / 매장 ID / 매장 ID(주문 가능한 것만)
 * 
 * 생성 
 * 메뉴 Dto
 * 
 * 수정
 * 수정하고자 하는 메뉴 ID 및 수정된 메뉴 Dto
 * 
 * 삭제
 * 메뉴 ID
*/
public interface MenuService {  
    MenuDto getMenuById(Long id);
    List<MenuDto> getAllMenus();    
    List<MenuDto> getMenusByStore(Long storeId);  
    List<MenuDto> getAvailableMenusByStore(Long storeId); 
    MenuDto createMenu(MenuDto menuDto);    
    MenuDto updateMenu(Long id, MenuDto menuDto);   
    void deleteMenu(Long id);
}