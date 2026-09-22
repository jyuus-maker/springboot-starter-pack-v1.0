package com.example.shop01.repository;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.domain.Item;
import com.example.shop01.domain.QItem;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@Transactional
public class ItemRepositoryTest3 {

    @PersistenceContext
    EntityManager em;

    public void createItemList() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemName("쿼리DSL테스트 상품 " + i);
            item.setPrice(10000 + (i * 1000));
            item.setItemDetail("쿼리DSL테스트 상품 설명 " + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            em.persist(item);
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트")
    public void querydslTest() {
        this.createItemList();

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;

        JPAQuery<Item> query = queryFactory
                .selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%쿼리DSL테스트 상품 설명%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        assertThat(itemList).isNotEmpty();
        assertThat(itemList).hasSize(10);
        assertThat(itemList.get(0).getPrice()).isGreaterThan(itemList.get(9).getPrice());

        for (Item item : itemList) {
            log.info("QueryDSL Result Item: {}", item);
        }
    }
}
