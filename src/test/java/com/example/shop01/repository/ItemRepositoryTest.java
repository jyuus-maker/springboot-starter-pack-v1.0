package com.example.shop01.repository;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.domain.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    private void createTestItems(String prefix, int count) {
        for (int i = 1; i <= count; i++) {
            Item item = new Item();
            item.setItemName(prefix + " " + i);
            item.setPrice(10000 + (i * 1000));
            item.setItemDetail(prefix + " 상세 설명 " + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 10개 저장 테스트")
    public void createItemListTest() {
        createTestItems("저장테스트상품", 10);
        List<Item> items = itemRepository.findByItemName("저장테스트상품 1");
        assertThat(items).isNotEmpty();
        assertThat(items.get(0).getItemName()).isEqualTo("저장테스트상품 1");
    }

    @Test
    @DisplayName("상품명 검색 테스트")
    public void findByItemNameTest() {
        createTestItems("검색테스트상품", 5);
        List<Item> itemList = itemRepository.findByItemName("검색테스트상품 1");
        assertThat(itemList).isNotEmpty();
        assertThat(itemList.get(0).getItemName()).isEqualTo("검색테스트상품 1");
    }

    @Test
    @DisplayName("가격 이하 검색")
    public void findByPriceLessThanTest() {
        createTestItems("가격필터상품", 10);
        List<Item> itemList = itemRepository.findByPriceLessThan(15000);
        assertThat(itemList).isNotEmpty();
        for (Item item : itemList) {
            assertThat(item.getPrice()).isLessThan(15000);
        }
    }

    @Test
    @DisplayName("가격 내림차순 검색")
    public void findByPriceLessThanOrderByPriceDescTest() {
        createTestItems("정렬상품", 10);
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(15000);
        assertThat(itemList).isNotEmpty();
        for (int i = 0; i < itemList.size() - 1; i++) {
            assertThat(itemList.get(i).getPrice()).isGreaterThanOrEqualTo(itemList.get(i + 1).getPrice());
        }
    }

    @Test
    @DisplayName("기본 단언문(Assertion) Not-Null 검증 테스트")
    public void junitTest() {
        String name1 = "홍길동";
        String name2 = "김자바";

        assertThat(name1).isNotNull();
        assertThat(name2).isNotNull();
    }

    @Test
    @DisplayName("모든 상품 조회")
    void findAllAndItemReturn() {
        long beforeCount = itemRepository.count();
        createTestItems("전체조회상품", 10);
        List<Item> itemList = itemRepository.findAll();

        assertThat(itemList).isNotNull();
        assertThat(itemList.size()).isEqualTo(beforeCount + 10);
    }
}
