package com.example.shop01.dto;

import com.example.shop01.constant.ItemSellStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 상품 기본 정보 데이터 전달 객체 (DTO)
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id; // 상품 식별자 (PK)

    private String itemName; // 상품명

    private int price; // 가격

    private int stockNumber; // 재고수량

    private String itemDetail; // 상품 상세설명

    private ItemSellStatus itemSellStatus; // 상품 판매 상태 (SELL, SOLD_OUT)

    private LocalDateTime regTime; // 등록시간

    private LocalDateTime updateTime; // 수정시간

    private String createdBy; // 등록자

    private String modifiedBy; // 수정자

} //end class ItemDto
