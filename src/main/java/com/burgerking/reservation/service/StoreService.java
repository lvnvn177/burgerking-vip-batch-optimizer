package com.burgerking.reservation.service;

import com.burgerking.reservation.web.dto.StoreDto;

import java.util.List;

public interface StoreService {
    StoreDto getStoreById(Long id);
    List<StoreDto> getAllStores();
    List<StoreDto> getOpenStores();
    StoreDto createStore(StoreDto storeDto);
    StoreDto updateStore(Long id, StoreDto storeDto);
    void deleteStore(Long id);
}