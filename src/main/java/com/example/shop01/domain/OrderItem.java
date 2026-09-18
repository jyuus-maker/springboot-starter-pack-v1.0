package com.example.shop01.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 주문 상품 엔티티 (OrderItem)
 * - 하나의 주문(Order)에 포함된 개별 상품(Item) 정보와 주문 당시의 가격, 수량을 관리하는 엔티티
 */
@Entity
@Table(name = "order_item")
@Getter
@Setter
@ToString(exclude = {"order", "item"})
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    // 하나의 상품은 여러 주문 상품에 포함될 수 있음 (N:1 다대일 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 하나의 주문에 여러 주문 상품이 포함됨 (N:1 다대일 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 당시 상품 가격

    private int count; // 주문 수량

    /**
     * 주문할 상품과 수량을 통해 OrderItem 엔티티를 생성하고 재고를 차감하는 팩토리 메서드
     *
     * @param item  주문할 상품 엔티티
     * @param count 주문 수량
     * @return 생성된 OrderItem 엔티티
     */
    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item != null ? item.getPrice() : 0);

        // 주문 시 재고 차감
        if (item != null) {
            item.removeStock(count);
        }

        return orderItem;
    } //end createOrderItem

    /**
     * 해당 주문 상품의 총 주문 금액 계산 (주문 가격 * 수량)
     *
     * @return 총 금액
     */
    public int getTotalPrice() {
        return this.orderPrice * this.count;
    } //end getTotalPrice

    /**
     * 주문 취소 시 상품 재고를 원복하는 비즈니스 메서드
     */
    public void cancel() {
        if (this.item != null) {
            this.item.addStock(this.count);
        }
    } //end cancel

} //end class OrderItem
