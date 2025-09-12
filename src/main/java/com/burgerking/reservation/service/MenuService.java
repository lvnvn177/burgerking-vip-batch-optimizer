package com.burgerking.reservation.service;

import com.burgerking.reservation.web.dto.MenuDto;

import java.util.List;

public interface MenuService {
    MenuDto getMenuById(Long id);
    List<MenuDto> getAllMenus();
    List<MenuDto> getMenusByStore(Long storeId);
    List<MenuDto> getAvailableMenusByStore(Long storeId);
    MenuDto createMenu(MenuDto menuDto);
    MenuDto updateMenu(Long id, MenuDto menuDto);
    void deleteMenu(Long id);
}