package com.burgerking.reservation.web;

import com.burgerking.reservation.service.MenuService;
import com.burgerking.reservation.web.dto.MenuDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/** 
 * 메뉴 관련 Controller
 * 메뉴 조회, 추가, 수정, 삭제 요청 
 * 
 * 메뉴 조회 필터링 
 * 전체 / 메뉴 ID / 매장 ID / 매장 ID(주문 가능한 것만) 
*/
@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<MenuDto>> getAllMenus() {    
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDto> getMenuById(@PathVariable Long id) { 
        return ResponseEntity.ok(menuService.getMenuById(id));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<MenuDto>> getMenusByStore(@PathVariable Long storeId) {  
        return ResponseEntity.ok(menuService.getMenusByStore(storeId));
    }

    @GetMapping("/store/{storeId}/available")
    public ResponseEntity<List<MenuDto>> getAvailableMenusByStore(@PathVariable Long storeId) { 
        return ResponseEntity.ok(menuService.getAvailableMenusByStore(storeId));
    }

    @PostMapping
    public ResponseEntity<MenuDto> createMenu(@Valid @RequestBody MenuDto menuDto) { 
        return new ResponseEntity<>(menuService.createMenu(menuDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}") 
    public ResponseEntity<MenuDto> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuDto menuDto) {
        return ResponseEntity.ok(menuService.updateMenu(id, menuDto));
    }

    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }
}