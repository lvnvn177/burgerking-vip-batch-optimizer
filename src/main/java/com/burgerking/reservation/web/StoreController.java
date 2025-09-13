package com.burgerking.reservation.web;

import com.burgerking.reservation.service.StoreService;
import com.burgerking.reservation.web.dto.StoreDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/** 
 * 매장 관련 Controller
 * 매장 조회, 추가, 수정, 삭제 요청 
 * 
 * 매장 조회 필터링 
 * 전체 / 오픈 여부 / 매장 ID
 * 
 * 
*/
@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public ResponseEntity<List<StoreDto>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/open")
    public ResponseEntity<List<StoreDto>> getOpenStores() {
        return ResponseEntity.ok(storeService.getOpenStores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @PostMapping
    public ResponseEntity<StoreDto> createStore(@Valid @RequestBody StoreDto storeDto) {
        return new ResponseEntity<>(storeService.createStore(storeDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDto> updateStore(@PathVariable Long id, @Valid @RequestBody StoreDto storeDto) {
        return ResponseEntity.ok(storeService.updateStore(id, storeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }
}