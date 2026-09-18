package com.example.shop01.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * [미인증 사용자 요청 처리 커스텀 진입점 (CustomAuthenticationEntryPoint)]
 *
 * 인증되지 않은(비로그인) 사용자가 로그인에 필요한 리소스에 접근했을 때
 * 요청 유형(AJAX 비동기 요청 vs 일반 브라우저 요청)에 따라 분기 처리합니다.
 *
 * 1) AJAX 요청: 401 Unauthorized 에러 코드를 반환하여 프론트엔드(JavaScript)에서 에러를 감지
 * 2) 일반 요청: 생성자로 전달받은 defaultEntryPoint(로그인 페이지 이동)로 위임 처리
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint defaultEntryPoint;

    /**
     * 기본 진입점(예: LoginUrlAuthenticationEntryPoint)을 전달받는 생성자
     *
     * @param defaultEntryPoint 일반 요청 시 위임할 기본 AuthenticationEntryPoint
     */
    public CustomAuthenticationEntryPoint(AuthenticationEntryPoint defaultEntryPoint) {
        this.defaultEntryPoint = defaultEntryPoint;
    } //end CustomAuthenticationEntryPoint

    /**
     * 인증 예외 발생 시 요청 헤더를 검사하여 분기 처리
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 1. AJAX(비동기) 요청인지 확인 (X-Requested-With = XMLHttpRequest)
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            // 401 Unauthorized 상태 코드를 반환하여 JS(클라이언트)에서 처리하도록 함
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        } else {
            // 2. 일반 웹 브라우저 요청인 경우 기본 진입점(로그인 페이지 이동 등)으로 위임
            if (this.defaultEntryPoint != null) {
                this.defaultEntryPoint.commence(request, response, authException);
            } else {
                response.sendRedirect("/members/login");
            }
        }
    } //end commence
}
