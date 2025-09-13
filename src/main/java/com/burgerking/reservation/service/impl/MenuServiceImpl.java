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

/** 
 * 메뉴 관련 구현체
 * 
 * 조회
 * 메뉴 ID / 전체 / 해당 메뉴를 주문 가능한 매장 ID / 현재 해당 메뉴를 주문 가능한 매장 ID 
 * 
 * 생성 
 * 메뉴를 추가하고자 하는 매장 ID 
 * 
 * 수정
 * 수정하고자 하는 메뉴 ID 및 수정된 메뉴 Dto
 * 
 * 삭제
 * 메뉴 ID
 * 
 * 변환
 * 메뉴 Entity -> Dto, Dto -> Entity
 * 
*/
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
    public MenuDto getMenuById(Long id) { // Menu Id로 Menu 찾기 
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + id));
        return convertToDto(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto> getAllMenus() { // 전체 메뉴 찾기 
        return menuRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto> getMenusByStore(Long storeId) { // 매장 ID로 메뉴 찾기 
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + storeId));
        
        return menuRepository.findByStore(store).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDto> getAvailableMenusByStore(Long storeId) { // 매장 ID로 해당 매장에서 주문 가능한 메뉴 찾기 
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + storeId));
        
        return menuRepository.findByStoreAndAvailableTrue(store).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MenuDto createMenu(MenuDto menuDto) {  // 특정 매장에 새로운 메뉴 추가 
        Store store = storeRepository.findById(menuDto.getStoreId())
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + menuDto.getStoreId()));
        
        Menu menu = convertToEntity(menuDto, store);
        Menu savedMenu = menuRepository.save(menu);
        return convertToDto(savedMenu);
    }

    @Override
    @Transactional
    public MenuDto updateMenu(Long id, MenuDto menuDto) { // 메뉴 정보 수정
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
    public void deleteMenu(Long id) {   // 메뉴 ID로 해당 메뉴 삭제 
        menuRepository.deleteById(id);
    }

    private MenuDto convertToDto(Menu menu) {   // Menu 데이터를 Dto 형태로 변환
        return MenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .storeId(menu.getStore().getId())
                .available(menu.isAvailable())
                .build();
    }

    private Menu convertToEntity(MenuDto menuDto, Store store) { // Menu Dto를 엔티티 형태로 변환 
        return Menu.builder()
                .name(menuDto.getName())
                .price(menuDto.getPrice())
                .store(store)
                .available(menuDto.isAvailable())
                .build();
    }
}