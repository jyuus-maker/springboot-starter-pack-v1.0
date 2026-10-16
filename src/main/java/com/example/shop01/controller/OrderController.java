package com.example.shop01.controller;

import com.example.shop01.dto.ApiResponse;
import com.example.shop01.dto.OrderDto;
import com.example.shop01.dto.OrderHistDto;
import com.example.shop01.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

/**
 * [주문 및 구매 이력 컨트롤러 - OrderController]
 *
 * 상품 단건 바로 주문, 구매 내역 페이징 조회, 주문 취소(재고 환원) 요청을 총괄합니다.
 * 모든 비동기(AJAX) 요청은 표준 ApiResponse 규격으로 통일하여 응답합니다.
 */
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * [상품 단건 바로 주문하기 (비동기 REST API)]
     *
     * @param orderDto      주문 요청 DTO (상품 ID, 수량)
     * @param bindingResult 유효성 검증 결과
     * @param principal     현재 로그인한 사용자 정보
     * @return 생성된 주문 ID를 포함한 표준 ApiResponse
     */
    @PostMapping(value = "/order")
    @ResponseBody
    public ResponseEntity<ApiResponse<Long>> order(@RequestBody @Valid OrderDto orderDto,
                                                  BindingResult bindingResult,
                                                  Principal principal) {
        // 1. 요청 파라미터 유효성 검사
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

        // 2. 로그인 사용자 인증 검증
        if (principal == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요한 서비스입니다."));
        }

        // 3. 주문 서비스 호출 (원자적 트랜잭션: 재고 차감 및 주문 생성)
        String email = principal.getName();
        Long orderId = orderService.order(orderDto, email);

        return ResponseEntity.ok(ApiResponse.success(orderId, "주문이 성공적으로 접수되었습니다."));
    }

    /**
     * [구매 이력 목록 페이지 조회 (SSR Thymeleaf)]
     *
     * @param page      조회할 페이지 번호 (Optional)
     * @param principal 현재 로그인한 사용자 정보
     * @param model     뷰 전달용 모델 객체
     * @return 구매 내역 뷰 경로 (order/orderHist)
     */
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page,
                            Principal principal,
                            Model model) {
        if (principal == null) {
            return "redirect:/members/login";
        }

        // 한 페이지당 4건의 주문 내역 표시
        Pageable pageable = PageRequest.of(page.orElse(0), 4);
        Page<OrderHistDto> ordersHistPage = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", ordersHistPage);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

    /**
     * [주문 취소 요청 (비동기 REST API)]
     *
     * @param orderId   취소할 주문 ID
     * @param principal 현재 로그인한 사용자 정보
     * @return 취소된 주문 ID를 포함한 표준 ApiResponse
     */
    @PostMapping(value = "/order/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity<ApiResponse<Long>> cancelOrder(@PathVariable("orderId") Long orderId,
                                                        Principal principal) {
        if (principal == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인이 필요한 서비스입니다."));
        }

        // 본인의 주문인지 권한 검증
        if (!orderService.validateOrder(orderId, principal.getName())) {
            throw new AccessDeniedException("해당 주문에 대한 취소 권한이 없습니다.");
        }

        // 주문 취소 및 재고 환원 처리
        orderService.cancelOrder(orderId);

        return ResponseEntity.ok(ApiResponse.success(orderId, "주문이 정상적으로 취소되었습니다."));
    }
}
