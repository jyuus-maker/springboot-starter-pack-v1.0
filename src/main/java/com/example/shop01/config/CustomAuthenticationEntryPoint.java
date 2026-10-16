package com.example.shop01.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * [미인증 사용자 요청 처리 커스텀 진입점 (CustomAuthenticationEntryPoint)]
 *
 * 로그인하지 않은 사용자가 인증이 필요한 보호된 자원에 접근했을 때,
 * 클라이언트의 요청 성격(AJAX/REST API vs 일반 브라우저 페이지 탐색)을 자동 판별하여 대응합니다.
 *
 * 1) 비동기(AJAX / JSON) 요청:
 *    - 401 Unauthorized 상태 코드와 함께 표준 JSON 규격(ApiResponse) 반환
 *    - X-Requested-With 헤더 또는 Accept: application/json 헤더 감지
 * 2) 일반 웹 브라우저 요청:
 *    - 로그인 페이지(/members/login)로 302 리다이렉트
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint defaultEntryPoint;

    /**
     * 기본 진입점(LoginUrlAuthenticationEntryPoint)을 전달받는 생성자
     *
     * @param defaultEntryPoint 일반 요청 시 위임할 기본 AuthenticationEntryPoint
     */
    public CustomAuthenticationEntryPoint(AuthenticationEntryPoint defaultEntryPoint) {
        this.defaultEntryPoint = defaultEntryPoint;
    }

    /**
     * 인증 예외 발생 시 요청 헤더를 정밀 검사하여 분기 처리
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        String requestedWith = request.getHeader("X-Requested-With");
        String acceptHeader = request.getHeader("Accept");
        String contentType = request.getHeader("Content-Type");

        boolean isAjaxOrApi = "XMLHttpRequest".equals(requestedWith)
                || (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE))
                || (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE));

        if (isAjaxOrApi) {
            // 1. 비동기 요청 시: HTTP 401 상태 코드와 표준 ApiResponse JSON 반환
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"로그인이 필요한 서비스입니다.\",\"data\":null}");
        } else {
            // 2. 일반 브라우저 요청 시: 로그인 페이지로 리다이렉트
            if (this.defaultEntryPoint != null) {
                this.defaultEntryPoint.commence(request, response, authException);
            } else {
                response.sendRedirect("/members/login");
            }
        }
    }
}
