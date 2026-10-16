package com.example.shop01.service;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.constant.OrderStatus;
import com.example.shop01.domain.Item;
import com.example.shop01.domain.Member;
import com.example.shop01.domain.Order;
import com.example.shop01.dto.MemberFormDto;
import com.example.shop01.dto.OrderDto;
import com.example.shop01.repository.ItemRepository;
import com.example.shop01.repository.MemberRepository;
import com.example.shop01.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [주문 및 재고 차감/복구 비즈니스 로직 테스트 - OrderServiceTest]
 */
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Item saveTestItem(String name, int price, int stock) {
        Item item = new Item();
        item.setItemName(name);
        item.setPrice(price);
        item.setStockNumber(stock);
        item.setItemDetail(name + " 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        return itemRepository.save(item);
    }

    private Member saveTestMember(String email) {
        MemberFormDto dto = new MemberFormDto();
        dto.setEmail(email);
        dto.setName("주문테스터");
        dto.setAddress("서울시 서초구 반포대로 1");
        dto.setPassword("12345678");
        return memberService.registerMember(dto);
    }

    @Test
    @DisplayName("단일 상품 주문 성공 및 실시간 재고 차감 검증")
    void orderSuccessAndStockDeductionTest() {
        Item item = saveTestItem("테크 플렉스 레깅스", 69000, 100);
        Member member = saveTestMember("order_test_01@rehmia.com");

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(item.getId());
        orderDto.setCount(5);

        Long orderId = orderService.order(orderDto, member.getEmail());
        assertNotNull(orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(OrderStatus.ORDER, order.getOrderStatus());
        assertEquals(1, order.getOrderItems().size());

        // 재고가 100 -> 95로 정확히 차감되었는지 검증
        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals(95, updatedItem.getStockNumber());
    }

    @Test
    @DisplayName("재고 수량 초과 주문 시 IllegalStateException 발생 및 롤백 검증")
    void orderOutOfStockExceptionTest() {
        Item item = saveTestItem("한정판 윈드브레이커", 149000, 10);
        Member member = saveTestMember("order_test_02@rehmia.com");

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(item.getId());
        orderDto.setCount(15); // 재고 10개 초과 주문

        assertThrows(IllegalStateException.class, () -> {
            orderService.order(orderDto, member.getEmail());
        });

        // 롤백되어 재고가 여전히 10개로 보존되는지 확인
        Item currentItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals(10, currentItem.getStockNumber());
    }

    @Test
    @DisplayName("주문 취소 시 주문상태 CANCEL 변경 및 차감된 재고 자동 환원 검증")
    void cancelOrderAndStockRestorationTest() {
        Item item = saveTestItem("프리미엄 요가매트", 45000, 50);
        Member member = saveTestMember("order_test_03@rehmia.com");

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(item.getId());
        orderDto.setCount(10);

        Long orderId = orderService.order(orderDto, member.getEmail());
        Item orderedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals(40, orderedItem.getStockNumber()); // 50 -> 40

        // 주문 취소 실행
        orderService.cancelOrder(orderId);

        Order canceledOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderStatus.CANCEL, canceledOrder.getOrderStatus());

        // 재고가 40 -> 50으로 완벽히 복원되었는지 검증
        Item restoredItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals(50, restoredItem.getStockNumber());
    }
}
