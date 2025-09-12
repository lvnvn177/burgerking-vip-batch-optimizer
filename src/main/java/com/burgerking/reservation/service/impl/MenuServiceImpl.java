package com.burgerking.reservation.service.impl;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.repository.MenuRepository;
import com.burgerking.reservation.repository.StoreRepository;
import com.burgerking.reservation.service.MenuService;
import com.burgerking.reservation.web.dto.MenuDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    public MenuServiceImpl(MenuRepository menuRepository, StoreRepository storeRepository) {
    this.menuRepository = menuRepository;
    this.storeRepository = storeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MenuDto getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + id));
        return convertToDto(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto> getAllMenus() {
        return menuRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto> getMenusByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + storeId));
        
        return menuRepository.findByStore(store).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto> getAvailableMenusByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + storeId));
        
        return menuRepository.findByStoreAndAvailableTrue(store).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MenuDto createMenu(MenuDto menuDto) {
        Store store = storeRepository.findById(menuDto.getStoreId())
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + menuDto.getStoreId()));
        
        Menu menu = convertToEntity(menuDto, store);
        Menu savedMenu = menuRepository.save(menu);
        return convertToDto(savedMenu);
    }

    @Override
    @Transactional
    public MenuDto updateMenu(Long id, MenuDto menuDto) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + id));
        
        Store store = storeRepository.findById(menuDto.getStoreId())
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + menuDto.getStoreId()));
        
        menu.setName(menuDto.getName());
        menu.setPrice(menuDto.getPrice());
        menu.setStore(store);
        menu.setAvailable(menuDto.isAvailable());
        
        Menu updatedMenu = menuRepository.save(menu);
        return convertToDto(updatedMenu);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    private MenuDto convertToDto(Menu menu) {
        return MenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .storeId(menu.getStore().getId())
                .available(menu.isAvailable())
                .build();
    }

    private Menu convertToEntity(MenuDto menuDto, Store store) {
        return Menu.builder()
                .name(menuDto.getName())
                .price(menuDto.getPrice())
                .store(store)
                .available(menuDto.isAvailable())
                .build();
    }
}