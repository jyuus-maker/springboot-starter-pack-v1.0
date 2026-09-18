package com.example.shop01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 장바구니 페이지에서 선택한 상품들을 주문할 때 요청 데이터를 전달받기 위한 DTO
 * - 개별 장바구니 상품 ID(cartItemId) 및 여러 상품을 일괄 주문할 때의 목록(cartOrderDtoList)을 함께 처리
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartOrderDto {

    private Long cartItemId; // 주문할 장바구니 상품 ID

    private List<CartOrderDto> cartOrderDtoList; // 장바구니에서 여러 상품을 선택하여 일괄 주문 시 전달되는 리스트

} //end class CartOrderDto
