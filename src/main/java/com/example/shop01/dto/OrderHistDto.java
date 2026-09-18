package com.example.shop01.dto;

import com.example.shop01.constant.OrderStatus;
import com.example.shop01.domain.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * [주문 이력 조회 DTO - OrderHistDto]
 * - 구매 이력 페이지(orderHist.html)에서 한 건의 주문(Order) 정보와 포함된 상품 목록(orderItemDtoList)을 전달
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderHistDto {

    private Long orderId;                      // 주문 식별 번호
    private String orderDate;                  // 주문 일자 (문자열 포맷)
    private OrderStatus orderStatus;           // 주문 상태 (ORDER, CANCEL)
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>(); // 주문 상품 목록

    public OrderHistDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate() != null
                ? order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "";
        this.orderStatus = order.getOrderStatus();
    } //end constructor

    /**
     * 주문 상품 DTO를 리스트에 추가
     *
     * @param orderItemDto 주문 상품 DTO
     */
    public void addOrderItemDto(OrderItemDto orderItemDto) {
        this.orderItemDtoList.add(orderItemDto);
    } //end addOrderItemDto

} //end class OrderHistDto
