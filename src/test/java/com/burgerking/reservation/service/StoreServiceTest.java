package com.burgerking.reservation.service;

import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.repository.StoreRepository;
import com.burgerking.reservation.service.impl.StoreServiceImpl;
import com.burgerking.reservation.web.dto.StoreDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * StoreService의 단위 테스트 클래스입니다.
 */
@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreServiceImpl storeService;

    private Store testStore;
    private StoreDto testStoreDto;

    @BeforeEach
    void setUp() {
        testStore = Store.builder()
                .id(1L)
                .isOpen(true)
                .build();

        testStoreDto = StoreDto.builder()
                .id(testStore.getId())
                .isOpen(testStore.isOpen())
                .build();
    }

    /**
     * 매장 ID로 매장 정보를 정확하게 조회하는지 테스트합니다.
     */
    @Test
    void getStoreById_ShouldReturnStore() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(testStore));

        StoreDto result = storeService.getStoreById(1L);

        assertNotNull(result);
        assertEquals(testStore.getId(), result.getId());
        assertEquals(testStore.isOpen(), result.isOpen());
        verify(storeRepository, times(1)).findById(1L);
    }

    /**
     * 모든 매장 목록을 정확하게 조회하는지 테스트합니다.
     */
    @Test
    void getAllStores_ShouldReturnAllStores() {
        when(storeRepository.findAll()).thenReturn(Arrays.asList(testStore));

        List<StoreDto> result = storeService.getAllStores();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testStore.getId(), result.get(0).getId());
        verify(storeRepository, times(1)).findAll();
    }

    /**
     * 현재 영업 중인 매장 목록만 정확하게 조회하는지 테스트합니다.
     */
    @Test
    void getOpenStores_ShouldReturnOpenStores() {
        when(storeRepository.findByIsOpenTrue()).thenReturn(Arrays.asList(testStore));

        List<StoreDto> result = storeService.getOpenStores();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testStore.getId(), result.get(0).getId());
        assertTrue(result.get(0).isOpen());
        verify(storeRepository, times(1)).findByIsOpenTrue();
    }

    /**
     * 새로운 매장 정보를 받아 정상적으로 생성하는지 테스트합니다.
     */
    @Test
    void createStore_ShouldReturnCreatedStore() {
        when(storeRepository.save(any(Store.class))).thenReturn(testStore);

        StoreDto result = storeService.createStore(testStoreDto);

        assertNotNull(result);
        assertEquals(testStore.getId(), result.getId());
        assertEquals(testStore.isOpen(), result.isOpen());
        verify(storeRepository, times(1)).save(any(Store.class));
    }
}