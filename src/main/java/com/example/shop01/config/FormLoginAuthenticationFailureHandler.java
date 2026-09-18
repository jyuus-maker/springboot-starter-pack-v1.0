package com.example.shop01.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * [폼 로그인 인증 실패 처리 핸들러 (FormLoginAuthenticationFailureHandler)]
 *
 * 사용자가 로그인 시도 시 인증에 실패했을 때(비밀번호 불일치, 계정 없음 등)
 * 실패 원인별 맞춤 에러 메시지를 생성하여 로그인 에러 페이지로 안전하게 리다이렉트하는 클래스입니다.
 */
@Component
public class FormLoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * 인증 실패 발생 시 자동 호출되는 메서드
     *
     * @param request   인증 실패가 일어난 요청 객체
     * @param response  클라이언트에게 보낼 응답 객체
     * @param exception 인증 처리 중 발생한 세부 예외(AuthenticationException)
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMessage;

        // 1. 발생한 예외(Exception) 유형에 따라 사용자 친화적인 메시지 분기 처리
        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디(이메일) 또는 비밀번호가 일치하지 않습니다.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부 시스템 문제로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 계정입니다. 회원가입 후 로그인해 주세요.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
        } else {
            errorMessage = "알 수 없는 이유로 로그인에 실패했습니다. 다시 시도해 주세요.";
        }

        // 2. 한글 메시지 깨짐 방지를 위해 UTF-8 URL 인코딩 수행
        String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // 3. 에러 메시지를 쿼리 스트링에 담아 로그인 실패 화면으로 리다이렉트
        response.sendRedirect("/members/login/error?errorMessage=" + encodedErrorMessage);
    } //end onAuthenticationFailure
}
