package com.example.shop01.service;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.domain.Item;
import com.example.shop01.dto.ItemFormDto;
import com.example.shop01.dto.ItemSearchDto;
import com.example.shop01.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [상품 등록, 상세 조회, 검색/페이징 비즈니스 로직 테스트 - ItemServiceTest]
 */
@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    private ItemFormDto createItemFormDto(String name, int price, int stock) {
        ItemFormDto dto = new ItemFormDto();
        dto.setItemName(name);
        dto.setPrice(price);
        dto.setStockNumber(stock);
        dto.setItemDetail(name + " 상품에 대한 상세 설명입니다.");
        dto.setItemSellStatus(ItemSellStatus.SELL);
        return dto;
    }

    @Test
    @DisplayName("상품 신규 등록 및 상세 조회 검증")
    void saveItemAndGetDtlTest() {
        ItemFormDto itemFormDto = createItemFormDto("테크 런 플렉스 자켓", 89000, 50);

        Long savedItemId = itemService.saveItem(itemFormDto);
        assertNotNull(savedItemId);

        ItemFormDto retrievedItem = itemService.getItemDtl(savedItemId);
        assertNotNull(retrievedItem);
        assertEquals("테크 런 플렉스 자켓", retrievedItem.getItemName());
        assertEquals(89000, retrievedItem.getPrice());
        assertEquals(50, retrievedItem.getStockNumber());
        assertEquals(ItemSellStatus.SELL, retrievedItem.getItemSellStatus());
    }

    @Test
    @DisplayName("관리자 상품 목록 QueryDSL 동적 검색 및 페이징 검증")
    void getItemAdminPageTest() {
        itemService.saveItem(createItemFormDto("르미아 후디 블랙", 79000, 30));
        itemService.saveItem(createItemFormDto("르미아 후디 화이트", 79000, 20));
        itemService.saveItem(createItemFormDto("르미아 조거 팬츠", 69000, 40));

        ItemSearchDto searchDto = new ItemSearchDto();
        searchDto.setSearchDateType("all");
        searchDto.setSearchSellStatus(ItemSellStatus.SELL);
        searchDto.setSearchBy("itemName");
        searchDto.setSearchQuery("후디");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> itemPage = itemService.getItemAdminPage(searchDto, pageable);

        assertNotNull(itemPage);
        assertTrue(itemPage.getTotalElements() >= 2);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID 조회 시 EntityNotFoundException 발생 검증")
    void getNonExistentItemTest() {
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.getItemDtl(999999L);
        });
    }
}
