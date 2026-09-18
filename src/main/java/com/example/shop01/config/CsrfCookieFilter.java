package com.example.shop01.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * [CSRF 토큰 쿠키 발행 필터]
 *
 * Spring Security 6에서는 성능 최적화를 위해 CSRF 토큰을 '지연 로딩(Deferred Token)' 방식으로 처리합니다.
 * 이 필터는 매 HTTP 요청마다 CSRF 토큰의 값을 강제로 조회(getToken)하여,
 * 프론트엔드(React, Vue, SPA 또는 Thymeleaf 자바스크립트 등)가 읽을 수 있도록
 * 'XSRF-TOKEN' 쿠키가 응답 헤더에 정상적으로 발행/유지되도록 보장합니다.
 *
 * OncePerRequestFilter를 상속받아 하나의 HTTP 요청당 한 번만 안전하게 실행됩니다.
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    /**
     * 필터링 내부 로직 수행 메서드
     *
     * @param request     클라이언트 요청 객체
     * @param response    서버 응답 객체
     * @param filterChain 다음 보안 필터로 연결해주는 필터 체인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. HttpServletRequest 속성에 등록된 CsrfToken 객체를 가져옴
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        // 2. getToken()을 호출하여 지연 로딩되어 있던 토큰 값을 실제로 로드
        //    -> 이 호출을 통해 Spring Security의 CookieCsrfTokenRepository가 클라이언트 쿠키(XSRF-TOKEN)에 값을 굽게 됨
        if (csrfToken != null) {
            csrfToken.getToken();
        }

        // 3. 필터 체인의 다음 필터로 요청과 응답을 전달
        filterChain.doFilter(request, response);
    } //end doFilterInternal
}
