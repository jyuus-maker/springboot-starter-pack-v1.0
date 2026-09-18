package com.example.shop01.repository;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.domain.Item;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 10개 저장 테스트")
    public void createItemListTest() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemName("테스트 상품 " + i);
            item.setPrice(10000 + (i * 1000));
            item.setItemDetail("테스트 상품 상세 설명입니다. " + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);

            Item savedItem = itemRepository.save(item);
            System.out.println(savedItem.toString());
        }
    } //end createItemListTest

    @Test
    @DisplayName("상품명 검색 테스트")
    public void findByItemNameTest() {
        this.createItemListTest();
        List<Item> itemList = itemRepository.findByItemName("테스트 상품 1");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    } //end findByItemNameTest

    @Test
    @DisplayName("가격 이하 검색")
    public void findByPriceLessThanTest() {
        this.createItemListTest();
        List<Item> itemList = itemRepository.findByPriceLessThan(15000);
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    } //end findByPriceLessThanTest

    @Test
    @DisplayName("가격 내림차순 검색")
    public void findByPriceLessThanOrderByPriceDescTest() {
        this.createItemListTest();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(15000);
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    } //end findByPriceLessThanOrderByPriceDescTest

    @Test
    @DisplayName("기본 단언문(Assertion) Not-Null 검증 테스트")
    public void junitTest() {
        String name1 = "홍길동";
        String name2 = "김자바";

        assertThat(name1).isNotNull();
        assertThat(name2).isNotNull();
    } //end junitTest

    @Test
    @DisplayName("모든 상품 조회")
    void findAllAndItemReturn() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemName("상품 " + i);
            item.setPrice(10000 + (i * 1000));
            item.setItemDetail("상품 상세 설명 " + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);

            itemRepository.save(item);
        }

        List<Item> itemList = itemRepository.findAll();

        assertThat(itemList).isNotNull();
        assertThat(itemList).hasSize(10);
        assertThat(itemList.get(0).getItemName()).isEqualTo("상품 1");

        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    } //end findAllAndItemReturn
}
