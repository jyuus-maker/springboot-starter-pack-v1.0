package com.example.shop01.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @Id
    @Column(name = "cart_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 하나의 장바구니(Cart)에 여러 상품(CartItem)이 담김 (N:1 다대일 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 하나의 상품(Item)이 여러 장바구니 상품(CartItem)으로 담김 (N:1 다대일 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 장바구니에 담긴 해당 상품의 수량
    private int count;

    /**
     * 장바구니에 담을 상품 엔티티를 생성하는 팩토리 메서드
     *
     * @param cart  담길 장바구니
     * @param item  담길 상품
     * @param count 담을 수량
     * @return 생성된 CartItem 엔티티
     */
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    } //end createCartItem

    /**
     * 기존 장바구니에 담긴 상품의 수량을 추가하는 비즈니스 메서드
     *
     * @param count 추가할 수량
     */
    public void addCount(int count) {
        this.count += count;
    } //end addCount

    /**
     * 장바구니 상품의 수량을 변경(수정)하는 비즈니스 메서드
     *
     * @param count 변경할 수량
     */
    public void updateCount(int count) {
        this.count = count;
    } //end updateCount
}
