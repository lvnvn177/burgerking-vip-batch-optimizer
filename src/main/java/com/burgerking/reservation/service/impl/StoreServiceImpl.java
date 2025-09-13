package com.burgerking.reservation.service.impl;

import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.repository.StoreRepository;
import com.burgerking.reservation.service.StoreService;
import com.burgerking.reservation.web.dto.StoreDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** 
 * 매장 관련 구현체
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
 * 
 * 변환
 * Entity -> Dto / Dto -> Entity 
*/
@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

  
    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StoreDto getStoreById(Long id) { 
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + id));
        return convertToDto(store);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getAllStores() {  
        return storeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDto> getOpenStores() { 
        return storeRepository.findByIsOpenTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StoreDto createStore(StoreDto storeDto) {   
        Store store = convertToEntity(storeDto);
        Store savedStore = storeRepository.save(store);
        return convertToDto(savedStore);
    }

    @Override
    @Transactional
    public StoreDto updateStore(Long id, StoreDto storeDto) {  
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + id));
        
        store.setOpen(storeDto.isOpen());
        Store updatedStore = storeRepository.save(store);
        return convertToDto(updatedStore);
    }

    @Override
    @Transactional
    public void deleteStore(Long id) { 
        storeRepository.deleteById(id);
    }

    private StoreDto convertToDto(Store store) {   
        return StoreDto.builder()
                .id(store.getId())
                .isOpen(store.isOpen())
                .build();
    }

    private Store convertToEntity(StoreDto storeDto) {  
        return Store.builder()
                .isOpen(storeDto.isOpen())
                .build();
    }
}