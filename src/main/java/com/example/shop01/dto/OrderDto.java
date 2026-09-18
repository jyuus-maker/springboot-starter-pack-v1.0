package com.example.shop01.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 상품 상세 페이지에서 상품 주문 시 요청 데이터를 전달받기 위한 Data Transfer Object (DTO)
 * - 주문할 상품의 식별자(itemId)와 주문 수량(count)을 Controller 및 Service 계층으로 전달
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId; // 주문할 상품 식별자 (ID)

    @NotNull(message = "수량은 필수 입력 값입니다.")
    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개 입니다.")
    private Integer count; // 주문할 상품 수량

} //end class OrderDto
