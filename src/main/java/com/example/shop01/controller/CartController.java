package com.example.shop01.controller;

import com.example.shop01.dto.ApiResponse;
import com.example.shop01.dto.CartDetailDto;
import com.example.shop01.dto.CartItemDto;
import com.example.shop01.dto.CartOrderDto;
import com.example.shop01.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * [쇼핑백(장바구니) 컨트롤러 - CartController]
 *
 * 장바구니 상품 추가, 목록 조회, 수량 동적 변경, 개별 삭제, 선택 상품 일괄 주문을 총괄합니다.
 * 모든 비동기(AJAX) 요청은 표준 ApiResponse 규격으로 통일하여 응답합니다.
 */
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * [장바구니에 상품 담기 (비동기 REST API)]
     *
     * @param cartItemDto   담을 상품 ID 및 수량
     * @param bindingResult 유효성 검증 결과
     * @param principal     현재 로그인한 사용자 정보
     * @return 등록된 장바구니 아이템 ID를 포함한 표준 ApiResponse
     */
    @PostMapping(value = "/cart")
    @ResponseBody
    public ResponseEntity<ApiResponse<Long>> addCart(@RequestBody @Valid CartItemDto cartItemDto,
                                                    BindingResult bindingResult,
                                                    Principal principal) {
        // 1. DTO 유효성 검사 (수량 1개 이상 등)
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(fieldError.getDefaultMessage());
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(sb.toString()));
        }

        // 2. 인증 여부 검증
        if (principal == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요한 서비스입니다."));
        }

        // 3. 장바구니 담기 비즈니스 로직 수행
        String email = principal.getName();
        Long cartItemId = cartService.addCart(cartItemDto, email);

        return ResponseEntity.ok(ApiResponse.success(cartItemId, "상품을 쇼핑백에 담았습니다."));
    }

    /**
     * [장바구니 페이지 조회 (SSR Thymeleaf)]
     *
     * @param principal 현재 로그인한 사용자 정보
     * @param model     뷰 전달용 모델 객체
     * @return 장바구니 템플릿 경로 (cart/cartList)
     */
    @GetMapping(value = "/cart")
    public String cartList(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/members/login";
        }

        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartDetailList);

        return "cart/cartList";
    }

    /**
     * [장바구니 상품 수량 변경 (비동기 REST API)]
     *
     * @param cartItemId 장바구니 아이템 ID
     * @param count      변경할 수량
     * @param principal  현재 로그인한 사용자 정보
     * @return 업데이트된 장바구니 아이템 ID를 포함한 표준 ApiResponse
     */
    @PatchMapping(value = "/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Long>> updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                           @RequestParam("count") int count,
                                                           Principal principal) {
        if (count <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("최소 1개 이상 담아주세요."));
        }

        if (principal == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요한 서비스입니다."));
        }

        // 타인의 장바구니 아이템 조작 방지
        if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        cartService.updateCartItemCount(cartItemId, count);
        return ResponseEntity.ok(ApiResponse.success(cartItemId, "수량이 변경되었습니다."));
    }

    /**
     * [장바구니 상품 삭제 (비동기 REST API)]
     *
     * @param cartItemId 삭제할 장바구니 아이템 ID
     * @param principal  현재 로그인한 사용자 정보
     * @return 삭제된 장바구니 아이템 ID를 포함한 표준 ApiResponse
     */
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Long>> deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                           Principal principal) {
        if (principal == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요한 서비스입니다."));
        }

        // 타인의 장바구니 아이템 삭제 방지
        if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(ApiResponse.success(cartItemId, "쇼핑백에서 삭제되었습니다."));
    }

    /**
     * [장바구니 선택 상품 일괄 주문 (비동기 REST API)]
     *
     * @param cartOrderDto 주문 대상 목록이 담긴 DTO
     * @param principal    현재 로그인한 사용자 정보
     * @return 생성된 대표 주문 ID를 포함한 표준 ApiResponse
     */
    @PostMapping(value = "/cart/orders")
    @ResponseBody
    public ResponseEntity<ApiResponse<Long>> orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                                          Principal principal) {
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        // 1. 주문 상품 선택 유무 검증 (400 Bad Request)
        if (cartOrderDtoList == null || cartOrderDtoList.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("주문할 상품을 선택해주세요."));
        }

        // 2. 인증 검증
        if (principal == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요한 서비스입니다."));
        }

        // 3. 선택된 모든 아이템의 소유권 검증
        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if (!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())) {
                throw new AccessDeniedException("선택된 상품 중 주문 권한이 없는 상품이 포함되어 있습니다.");
            }
        }

        // 4. 일괄 주문 트랜잭션 수행 (재고 차감, 주문 엔티티 생성, 장바구니 비우기)
        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(orderId, "주문이 성공적으로 접수되었습니다."));
    }
}
