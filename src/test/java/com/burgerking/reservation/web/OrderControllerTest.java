package com.burgerking.reservation.web;

import com.burgerking.reservation.domain.Menu;
import com.burgerking.reservation.domain.Store;
import com.burgerking.reservation.domain.enums.OrderStatus;
import com.burgerking.reservation.repository.MenuRepository;
import com.burgerking.reservation.repository.StoreRepository;
import com.burgerking.reservation.web.dto.OrderRequest;
import com.burgerking.reservation.web.dto.OrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Store testStore;
    private Menu testMenu;

    @BeforeEach
    void setUp() {
        // 테스트용 매장 생성
        testStore = Store.builder()
                .isOpen(true)
                .build();
        testStore = storeRepository.save(testStore);

        // 테스트용 메뉴 생성
        testMenu = Menu.builder()
                .name("치즈버거")
                .price(new BigDecimal("6000"))
                .store(testStore)
                .available(true)
                .build();
        testMenu = menuRepository.save(testMenu);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        // 주문 요청 생성
        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setMenuId(testMenu.getId());
        item.setQuantity(2);
        items.add(item);

        OrderRequest orderRequest = OrderRequest.builder()
                .userId(1L)
                .storeId(testStore.getId())
                .pickupTime(LocalDateTime.now().plusHours(1))
                .items(items)
                .build();

        // 주문 생성 API 호출
        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.orderNumber").exists())
                .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.toString()))
                .andReturn();

        // 응답 결과 파싱
        OrderResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderResponse.class);

        // 주문 조회 API 호출
        mockMvc.perform(get("/api/orders/" + response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.orderNumber").value(response.getOrderNumber()));
    }
}