package com.example.shop01.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [표준 API 공통 응답 DTO - ApiResponse]
 *
 * 프론트엔드와 백엔드 간의 모든 비동기(REST API/AJAX) 통신 시
 * 일관된 JSON 포맷을 유지하기 위한 글로벌 표준 응답 래퍼(Wrapper) 클래스입니다.
 *
 * <응답 JSON 규격 예시>
 * 성공: { "success": true, "message": "주문이 완료되었습니다.", "data": { ... } }
 * 실패: { "success": false, "message": "상품 재고가 부족합니다.", "data": null }
 *
 * @param <T> 응답 본문 데이터 타입
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * 성공 여부 플래그 (true / false)
     */
    private boolean success;

    /**
     * 클라이언트에게 전달할 안내 또는 오류 메시지 (한국어)
     */
    private String message;

    /**
     * 실제 응답 데이터 (단건 객체, 리스트, 식별자 등)
     */
    private T data;

    /**
     * 성공 응답 생성 (데이터 + 메시지)
     *
     * @param data    반환할 데이터
     * @param message 안내 메시지
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 성공 응답 생성 (데이터만 반환)
     *
     * @param data 반환할 데이터
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    /**
     * 성공 응답 생성 (메시지만 반환, 데이터 없음)
     *
     * @param message 안내 메시지
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> successMessage(String message) {
        return success(null, message);
    }

    /**
     * 실패 응답 생성 (오류 메시지)
     *
     * @param message 클라이언트에 표시할 오류 사유
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * 실패 응답 생성 (오류 메시지 + 세부 데이터)
     *
     * @param message 클라이언트에 표시할 오류 사유
     * @param data    세부 에러 데이터 (예: 유효성 검증 오류 맵)
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
