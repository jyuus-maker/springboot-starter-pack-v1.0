package com.example.shop01.repository;

import com.example.shop01.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    // <쿼리메서드> - jpa가 제공
    // 찾기(검색) : find + (엔티티이름) + By + 검색에서_사용할_변수이름

    // itemName으로 검색
    List<Item> findByItemName(String itemName);

    // itemName 또는 stockNumber로 검색
    List<Item> findByItemNameOrStockNumber(String itemName, Integer stockNumber);
    
    // itemName 또는 itemDetail로 검색
    List<Item> findByItemNameOrItemDetail(String itemName, String itemDetail);

    List<Item> findByPriceLessThan(Integer price);
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    // 1. JPQL
    @Query("SELECT i FROM Item i WHERE i.itemName = :itemName")
    List<Item> findItems(@Param("itemName") String itemName);

    // 2. Native Query
    @Query(value = "SELECT * FROM item WHERE item_name = :itemName", nativeQuery = true)
    List<Item> findItemNative(@Param("itemName") String itemName);

} //end interface ItemRepository
