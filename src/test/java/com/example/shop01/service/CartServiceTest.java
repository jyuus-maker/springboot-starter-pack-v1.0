package com.example.shop01.service;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.domain.CartItem;
import com.example.shop01.domain.Item;
import com.example.shop01.domain.Member;
import com.example.shop01.domain.Order;
import com.example.shop01.dto.CartDetailDto;
import com.example.shop01.dto.CartItemDto;
import com.example.shop01.dto.CartOrderDto;
import com.example.shop01.dto.MemberFormDto;
import com.example.shop01.repository.CartItemRepository;
import com.example.shop01.repository.ItemRepository;
import com.example.shop01.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [장바구니 담기, 수량 변경, 일괄 주문 비즈니스 로직 테스트 - CartServiceTest]
 */
@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

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
        dto.setName("장바구니테스터");
        dto.setAddress("서울시 송파구 올림픽로 1");
        dto.setPassword("12345678");
        return memberService.registerMember(dto);
    }

    @Test
    @DisplayName("장바구니 상품 추가 및 조회 검증")
    void addCartAndGetListTest() {
        Item item = saveTestItem("러닝 쇼츠", 39000, 50);
        Member member = saveTestMember("cart_test_01@rehmia.com");

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setItemId(item.getId());
        cartItemDto.setCount(2);

        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());
        assertNotNull(cartItemId);

        List<CartDetailDto> cartList = cartService.getCartList(member.getEmail());
        assertFalse(cartList.isEmpty());
        assertEquals(1, cartList.size());
        assertEquals("러닝 쇼츠", cartList.get(0).getItemName());
        assertEquals(2, cartList.get(0).getCount());
    }

    @Test
    @DisplayName("장바구니 상품 수량 변경 검증")
    void updateCartItemCountTest() {
        Item item = saveTestItem("컴프레션 탑", 49000, 30);
        Member member = saveTestMember("cart_test_02@rehmia.com");

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setItemId(item.getId());
        cartItemDto.setCount(1);

        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());

        // 수량을 1 -> 4로 변경
        cartService.updateCartItemCount(cartItemId, 4);

        CartItem updatedCartItem = cartItemRepository.findById(cartItemId).orElseThrow();
        assertEquals(4, updatedCartItem.getCount());
    }

    @Test
    @DisplayName("장바구니 선택 상품 일괄 주문 및 장바구니 자동 비우기 검증")
    void orderCartItemTest() {
        Item item1 = saveTestItem("액티브 헤어밴드", 12000, 100);
        Item item2 = saveTestItem("스포츠 물병", 25000, 50);
        Member member = saveTestMember("cart_test_03@rehmia.com");

        CartItemDto dto1 = new CartItemDto();
        dto1.setItemId(item1.getId());
        dto1.setCount(2);
        Long cartItemId1 = cartService.addCart(dto1, member.getEmail());

        CartItemDto dto2 = new CartItemDto();
        dto2.setItemId(item2.getId());
        dto2.setCount(1);
        Long cartItemId2 = cartService.addCart(dto2, member.getEmail());

        List<CartOrderDto> orderList = new ArrayList<>();
        orderList.add(CartOrderDto.builder().cartItemId(cartItemId1).build());
        orderList.add(CartOrderDto.builder().cartItemId(cartItemId2).build());

        // 일괄 주문 실행
        Long orderId = cartService.orderCartItem(orderList, member.getEmail());
        assertNotNull(orderId);

        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(2, order.getOrderItems().size());

        // 주문된 장바구니 아이템이 장바구니에서 삭제되었는지 확인
        assertFalse(cartItemRepository.findById(cartItemId1).isPresent());
        assertFalse(cartItemRepository.findById(cartItemId2).isPresent());

        // 재고 정상 차감 확인 (item1: 100->98, item2: 50->49)
        assertEquals(98, itemRepository.findById(item1.getId()).orElseThrow().getStockNumber());
        assertEquals(49, itemRepository.findById(item2.getId()).orElseThrow().getStockNumber());
    }
}
