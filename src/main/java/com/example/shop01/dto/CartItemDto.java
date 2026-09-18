package com.example.shop01.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 장바구니 상품 요청/응답을 위한 Data Transfer Object (DTO)
 * - 상품 상세 페이지에서 장바구니에 상품을 담을 때 상품 ID 및 수량을 전달받는 용도
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId; // 장바구니에 담을 상품의 아이디

    @NotNull(message = "수량은 필수 입력 값입니다.")
    @Min(value = 1, message = "최소 1개 이상 담아주세요.")
    private Integer count; // 장바구니에 담을 상품의 수량

} //end class CartItemDto
