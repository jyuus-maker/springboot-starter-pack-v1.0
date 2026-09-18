package com.example.shop01.service;

import com.example.shop01.domain.*;
import com.example.shop01.dto.OrderDto;
import com.example.shop01.dto.OrderHistDto;
import com.example.shop01.dto.OrderItemDto;
import com.example.shop01.repository.ItemImgRepository;
import com.example.shop01.repository.ItemRepository;
import com.example.shop01.repository.MemberRepository;
import com.example.shop01.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * [주문 비즈니스 로직 서비스 - OrderService]
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    /**
     * 상품 단건 바로 주문하기
     *
     * @param orderDto 주문 요청 정보 (itemId, count)
     * @param email    주문 회원 이메일
     * @return 생성된 주문 ID
     */
    public Long order(OrderDto orderDto, String email) {
        // 1. 주문 상품 및 회원 엔티티 조회
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("주문할 상품을 찾을 수 없습니다."));
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("주문자 회원 정보를 찾을 수 없습니다."));

        // 2. 주문 상품(OrderItem) 생성 (내부에서 재고 차감 실행)
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        // 3. 주문(Order) 생성 및 저장
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    } //end order

    /**
     * 장바구니에서 여러 상품을 일괄 주문하기
     *
     * @param orderDtoList 주문 상품 DTO 목록
     * @param email        주문 회원 이메일
     * @return 생성된 주문 ID
     */
    public Long orders(List<OrderDto> orderDtoList, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("주문자 회원 정보를 찾을 수 없습니다."));

        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(() -> new EntityNotFoundException("주문할 상품을 찾을 수 없습니다."));

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    } //end orders

    /**
     * 회원의 구매 이력 목록 페이징 조회
     *
     * @param email    회원 이메일
     * @param pageable 페이징 정보
     * @return 구매 이력 DTO 페이징 객체 (Page<OrderHistDto>)
     */
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        // 1. 회원의 주문 목록 조회
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtoList = new ArrayList<>();

        // 2. 주문별로 포함된 주문 상품 목록 및 대표 이미지 DTO 조립
        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();

            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                String imgUrl = (itemImg != null) ? itemImg.getImgUrl() : "";
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, imgUrl);
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtoList.add(orderHistDto);
        }

        return new PageImpl<>(orderHistDtoList, pageable, totalCount != null ? totalCount : 0L);
    } //end getOrderList

    /**
     * 주문 취소 권한 검증 (로그인한 회원과 주문자 일치 여부)
     *
     * @param orderId 주문 ID
     * @param email   로그인 사용자 이메일
     * @return 본인 주문 여부
     */
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email).orElse(null);
        Order order = orderRepository.findById(orderId).orElse(null);

        if (curMember == null || order == null || order.getMember() == null) {
            return false;
        }

        return StringUtils.equals(curMember.getEmail(), order.getMember().getEmail());
    } //end validateOrder

    /**
     * 주문 취소 처리 (상태 CANCEL 변경 및 재고 복구)
     *
     * @param orderId 취소할 주문 ID
     */
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("취소할 주문 정보가 존재하지 않습니다."));
        order.cancelOrder();
    } //end cancelOrder

} //end class OrderService
