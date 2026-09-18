package com.example.shop01.service;

import com.example.shop01.domain.Cart;
import com.example.shop01.domain.CartItem;
import com.example.shop01.domain.Item;
import com.example.shop01.domain.Member;
import com.example.shop01.dto.CartDetailDto;
import com.example.shop01.dto.CartItemDto;
import com.example.shop01.dto.CartOrderDto;
import com.example.shop01.dto.OrderDto;
import com.example.shop01.repository.CartItemRepository;
import com.example.shop01.repository.CartRepository;
import com.example.shop01.repository.ItemRepository;
import com.example.shop01.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * [장바구니 비즈니스 로직 서비스 - CartService]
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    /**
     * 장바구니에 상품 담기
     *
     * @param cartItemDto 장바구니에 담을 상품 정보 (itemId, count)
     * @param email       로그인 회원 이메일
     * @return 장바구니 상품 ID
     */
    public Long addCart(CartItemDto cartItemDto, String email) {
        // 1. 회원 및 상품 엔티티 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("상품 정보를 찾을 수 없습니다."));

        // 2. 회원의 장바구니 조회 (없으면 새로 생성)
        Cart cart = cartRepository.findByMemberId(member.getId()).orElse(null);
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 3. 해당 상품이 이미 장바구니에 있는지 확인
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if (savedCartItem != null) {
            // 기존에 담긴 상품이면 수량 추가
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            // 새로 담는 상품이면 CartItem 생성 후 저장
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    } //end addCart

    /**
     * 회원의 장바구니 상품 상세 목록 조회
     *
     * @param email 회원 이메일
     * @return 장바구니 상품 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            return new ArrayList<>();
        }

        Cart cart = cartRepository.findByMemberId(member.getId()).orElse(null);
        if (cart == null) {
            return new ArrayList<>();
        }

        return cartItemRepository.findCartDetailDtoList(cart.getId());
    } //end getCartList

    /**
     * 장바구니 상품의 회원 소유권 검증
     *
     * @param cartItemId 장바구니 상품 ID
     * @param email      로그인 회원 이메일
     * @return 본인 소유 여부
     */
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        Member curMember = memberRepository.findByEmail(email).orElse(null);
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);

        if (curMember == null || cartItem == null) {
            return false;
        }

        Member savedMember = cartItem.getCart().getMember();
        return StringUtils.equals(curMember.getEmail(), savedMember.getEmail());
    } //end validateCartItem

    /**
     * 장바구니 상품 수량 변경
     *
     * @param cartItemId 장바구니 상품 ID
     * @param count      변경할 수량
     */
    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));
        cartItem.updateCount(count);
    } //end updateCartItemCount

    /**
     * 장바구니 상품 단건 삭제
     *
     * @param cartItemId 삭제할 장바구니 상품 ID
     */
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 장바구니 상품이 존재하지 않습니다."));
        cartItemRepository.delete(cartItem);
    } //end deleteCartItem

    /**
     * 장바구니에서 선택된 상품들 일괄 주문 처리 및 장바구니 비우기
     *
     * @param cartOrderDtoList 주문할 장바구니 상품 DTO 목록
     * @param email            회원 이메일
     * @return 생성된 주문 ID
     */
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        List<OrderDto> orderDtoList = new ArrayList<>();

        // 1. 장바구니 상품 목록을 OrderDto 목록으로 변환
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        // 2. 주문 실행
        Long orderId = orderService.orders(orderDtoList, email);

        // 3. 주문 완료된 장바구니 상품들 삭제
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElse(null);
            if (cartItem != null) {
                cartItemRepository.delete(cartItem);
            }
        }

        return orderId;
    } //end orderCartItem

} //end class CartService
