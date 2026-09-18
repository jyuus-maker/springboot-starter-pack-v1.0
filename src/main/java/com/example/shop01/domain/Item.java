package com.example.shop01.domain;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.dto.ItemDto;
import com.example.shop01.dto.ItemFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * [상품 엔티티 - Item]
 * - BaseEntity를 상속받아 등록일(regTime), 수정일(updateTime), 등록자(createdBy), 수정자(modifiedBy)를 JPA Auditing으로 자동 관리합니다.
 */
@Entity
@Table(name="item")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Item extends BaseEntity {

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //상품 코드(PK)

    @Column(nullable = false, length = 50, unique = true)
    private String itemName; //상품명

    @Column(name="price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Column(nullable = false, columnDefinition = "TEXT")
    private String itemDetail; //상품 상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    /**
     * ItemDto 객체를 받아 Item 엔티티 객체를 생성하는 팩토리 메서드
     *
     * @param dto 상품 정보가 담긴 DTO
     * @return 생성된 Item 엔티티
     */
    public static Item createItem(ItemDto dto) {
        Item item = new Item();
        item.itemName = dto.getItemName();
        item.price = dto.getPrice();
        item.stockNumber = dto.getStockNumber();
        item.itemDetail = dto.getItemDetail();
        item.itemSellStatus = dto.getItemSellStatus();
        return item;
    }//end createItem

    /**
     * 상품 정보 수정 비즈니스 메서드
     *
     * @param itemFormDto 수정할 상품 정보가 담긴 DTO
     */
    public void updateItem(ItemFormDto itemFormDto) {
        this.itemName = itemFormDto.getItemName();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    } //end updateItem

    /**
     * 상품 주문 시 재고 감소 비즈니스 메서드
     *
     * @param stockNumber 감소시킬 수량
     */
    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if (restStock < 0) {
            throw new IllegalStateException("상품의 재고가 부족합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    } //end removeStock

    /**
     * 주문 취소 시 재고 증가 비즈니스 메서드
     *
     * @param stockNumber 증가시킬 수량
     */
    public void addStock(int stockNumber) {
        this.stockNumber += stockNumber;
    } //end addStock

}//end class Item
