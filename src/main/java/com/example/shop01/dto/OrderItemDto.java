package com.example.shop01.dto;

import com.example.shop01.domain.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [주문 상품 정보 DTO - OrderItemDto]
 * - 구매 이력 페이지(orderHist.html)에서 주문에 포함된 개별 상품의 상세 정보를 전달
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderItemDto {

    private String itemName;   // 주문 상품명
    private int count;          // 주문 수량
    private int orderPrice;     // 주문 금액 (단가)
    private String imgUrl;      // 상품 대표 이미지 경로

    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        this.itemName = orderItem.getItem().getItemName();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    } //end constructor

} //end class OrderItemDto
