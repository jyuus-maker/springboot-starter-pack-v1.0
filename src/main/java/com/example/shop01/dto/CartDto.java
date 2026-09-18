package com.example.shop01.dto;

import com.example.shop01.domain.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니(Cart) 엔티티의 데이터를 전달하기 위한 Data Transfer Object (DTO)
 * - 장바구니 기본 정보 및 장바구니에 담긴 상품 목록을 뷰(UI)나 API 응답으로 전달하는 용도
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private Long id; // 장바구니 ID

    private Long memberId; // 장바구니 소유 회원 ID

    private String memberName; // 장바구니 소유 회원 이름

    @Builder.Default
    private List<CartItemDto> cartItems = new ArrayList<>(); // 장바구니에 담긴 상품 목록

    /**
     * Cart 엔티티를 기반으로 CartDto를 생성하는 팩토리 메서드
     *
     * @param cart 변환할 Cart 엔티티
     * @return 변환된 CartDto 객체
     */
    public static CartDto of(Cart cart) {
        if (cart == null) {
            return null;
        }

        return CartDto.builder()
                .id(cart.getId())
                .memberId(cart.getMember() != null ? cart.getMember().getId() : null)
                .memberName(cart.getMember() != null ? cart.getMember().getName() : null)
                .build();
    } //end of

} //end class CartDto
