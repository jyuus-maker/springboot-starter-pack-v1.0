package com.example.shop01.domain;

import com.example.shop01.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 엔티티 (Order)
 * - 회원의 주문 정보 및 주문에 포함된 상품(OrderItem) 목록을 관리하는 엔티티
 * - 데이터베이스 예약어와의 충돌을 피하기 위해 테이블명을 "orders"로 지정
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString(exclude = {"orderItems", "member"})
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id; // 주문 식별자

    // 한 명의 회원은 여러 번 주문 가능 (N:1 다대일 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; // 주문 날짜 및 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태 (ORDER: 주문완료, CANCEL: 주문취소)

    // 하나의 주문에 여러 주문 상품이 연결됨 (1:N 일대다 양방향 매핑, 영속성 전이)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * 연관관계 편의 메서드 - 주문 상품 추가
     * - Order와 OrderItem 양방향 연관관계를 설정
     *
     * @param orderItem 추가할 주문 상품
     */
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    } //end addOrderItem

    /**
     * 회원과 주문 상품 목록을 전달받아 주문 엔티티를 생성하는 팩토리 메서드
     *
     * @param member         주문한 회원
     * @param orderItemList  주문할 상품 목록
     * @return 생성된 Order 엔티티
     */
    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);

        if (orderItemList != null) {
            for (OrderItem orderItem : orderItemList) {
                order.addOrderItem(orderItem);
            }
        }

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    } //end createOrder

    /**
     * 주문 전체 금액을 계산하는 비즈니스 메서드
     *
     * @return 전체 주문 금액의 합
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : this.orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    } //end getTotalPrice

    /**
     * 주문 취소 비즈니스 메서드
     * - 주문 상태를 CANCEL로 변경하고, 포함된 모든 주문 상품의 재고를 원복합니다.
     */
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : this.orderItems) {
            orderItem.cancel();
        }
    } //end cancelOrder

} //end class Order
