package com.example.shop01.config;

import com.example.shop01.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * [글로벌 예외 처리기 - GlobalExceptionHandler]
 *
 * 애플리케이션 전역에서 발생하는 예외를 한곳에서 가로채어
 * 표준 ApiResponse 포맷과 적절한 HTTP 상태 코드로 일관되게 반환합니다.
 *
 * 컨트롤러마다 개별 try-catch를 중복 작성할 필요 없이,
 * 비즈니스 로직에서 발생한 예외를 안전하게 클라이언트 맞춤형 메시지로 변환합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [1. DTO 유효성 검증 실패 예외 처리 - MethodArgumentNotValidException]
     *
     * @Valid 어노테이션 검증에 실패했을 때 발생 (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        StringBuilder messageBuilder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            if (messageBuilder.length() > 0) messageBuilder.append(", ");
            messageBuilder.append(fieldError.getDefaultMessage());
        }

        String summaryMessage = messageBuilder.length() > 0 ? messageBuilder.toString() : "입력값이 올바르지 않습니다.";
        log.warn("[유효성 검증 실패] {}", summaryMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(summaryMessage, errors));
    }

    /**
     * [2. 비즈니스 로직 무결성 위반 예외 처리 - IllegalStateException]
     *
     * 상품 재고 부족, 잘못된 상태 전이 등 비즈니스 규칙 위반 시 발생 (400 Bad Request)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        log.warn("[비즈니스 규칙 위반] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * [3. 잘못된 인자 전달 예외 처리 - IllegalArgumentException]
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("[잘못된 요청 인자] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * [4. 리소스 부재 예외 처리 - EntityNotFoundException]
     *
     * 조회하려는 상품, 주문, 회원 등이 데이터베이스에 존재하지 않을 때 발생 (404 Not Found)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("[데이터 미존재] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * [5. 인가(권한) 거부 예외 처리 - AccessDeniedException]
     *
     * 타인의 주문 취소 시도 또는 비인가 리소스 접근 시 발생 (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("[접근 권한 거부] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("요청에 대한 권한이 없습니다."));
    }

    /**
     * [6. 기타 서버 내부 오류 처리 - Exception]
     *
     * 미처 처리하지 못한 런타임 예외 발생 시 보안상 세부 스택트레이스를 숨기고 안내 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("[서버 내부 오류 발생]", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버 처리 중 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}
