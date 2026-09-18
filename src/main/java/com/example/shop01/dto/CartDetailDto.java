package com.example.shop01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 장바구니 조회 페이지에서 장바구니에 담긴 상품 상세 정보를 화면에 전달하기 위한 DTO
 * - JPQL 생성자 프로젝션(new com.example.shop01.dto.CartDetailDto(...)) 및 화면 렌더링에 사용
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailDto {

    private Long cartItemId; // 장바구니 상품 ID

    private String itemName; // 상품명

    private int price; // 상품 가격

    private int count; // 장바구니에 담긴 수량

    private String imgUrl; // 상품 대표 이미지 경로 (URL)

} //end class CartDetailDto
