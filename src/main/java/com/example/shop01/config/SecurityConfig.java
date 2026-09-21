package com.example.shop01.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * [Spring Security 핵심 보안 설정 클래스]
 *
 * Spring Boot 3.x / Spring Security 6.x 표준 방식을 따르며,
 * 웹 애플리케이션의 인증(Authentication), 인가(Authorization), 로그인/로그아웃,
 * 정적 리소스 보안, 예외 처리 등의 보안 규칙을 빈(Bean)으로 등록하여 통합 관리합니다.
 */
@Configuration
@EnableWebSecurity // Spring Security 필터 체인을 활성화하고 웹 보안을 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final FormLoginAuthenticationFailureHandler authenticationFailureHandler;

    /**
     * [비밀번호 암호화 인코더 빈 등록]
     *
     * BCrypt 해시 알고리즘을 사용하여 사용자의 비밀번호를 단방향 암호화합니다.
     * 동일한 평문 비밀번호라도 매번 다른 솔트(Salt) 값을 결합하여 해싱하므로 높은 보안성을 제공합니다.
     *
     * @return BCryptPasswordEncoder 객체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } //end passwordEncoder

    /**
     * [미인증 사용자 예외 처리 커스텀 진입점(AuthenticationEntryPoint) 빈 등록]
     *
     * 비로그인 사용자가 보호된 리소스에 접근했을 때:
     * - AJAX 요청: 401 Unauthorized 에러 코드 반환
     * - 일반 브라우저 요청: LoginUrlAuthenticationEntryPoint를 통해 로그인 페이지(/members/login)로 리다이렉트
     *
     * @return CustomAuthenticationEntryPoint 인스턴스
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        // 일반 브라우저 요청 시 기본으로 이동할 로그인 페이지 진입점 생성
        LoginUrlAuthenticationEntryPoint loginUrlEntryPoint =
                new LoginUrlAuthenticationEntryPoint("/members/login");

        // CustomAuthenticationEntryPoint에 기본 진입점을 주입하여 반환
        return new CustomAuthenticationEntryPoint(loginUrlEntryPoint);
    } //end authenticationEntryPoint

    /**
     * [Spring Security HTTP 보안 필터 체인(SecurityFilterChain) 구성]
     *
     * 클라이언트로부터 들어오는 모든 HTTP 요청에 대한 보안 필터들의 처리 규칙을 정의합니다.
     *
     * @param http HttpSecurity 객체 (보안 설정 빌더)
     * @return 빌드된 SecurityFilterChain 인스턴스
     * @throws Exception 보안 구성 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ==============================================================================
                // 1. URL별 접근 권한(인가, Authorization) 설정
                // ==============================================================================
                .authorizeHttpRequests(auth -> auth
                        // 1) 비로그인 사용자 포함, 누구나 접근 가능한 공개 경로 (Permit All)
                        .requestMatchers(
                                "/",             // 메인 페이지
                                "/story",        // 브랜드 스토리 페이지
                                "/story/**",     // 브랜드 스토리 하위 경로
                                "/brand/**",     // 브랜드 관련 경로
                                "/members/**",   // 회원가입, 로그인 등 회원 관련 페이지
                                "/item/**",      // 상품 상세/조회 페이지
                                "/images/**",    // 상품 이미지 파일
                                "/css/**",       // 정적 스타일시트
                                "/js/**",        // 정적 자바스크립트 파일
                                "/favicon.ico",  // 웹사이트 파비콘
                                "/error"         // 스프링 기본 에러 페이지
                        ).permitAll()

                        // 2) 관리자 전용 경로 (ADMIN 역할을 가진 사용자만 접근 가능)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 3) 그 외의 모든 요청(장바구니, 주문, 마이페이지 등)은 반드시 로그인(인증) 필요
                        .anyRequest().authenticated()
                )

                // ==============================================================================
                // 2. 폼 기반 로그인(Form Login) 설정
                // ==============================================================================
                .formLogin(form -> form
                        .loginPage("/members/login")             // 커스텀 로그인 페이지 경로 (미인증 시 자동 리다이렉트)
                        .defaultSuccessUrl("/", true)           // 로그인 성공 시 기본으로 이동할 URL
                        .usernameParameter("email")              // 로그인 화면에서 아이디로 사용할 파라미터명 (기본값 username -> email로 커스텀)
                        .failureHandler(authenticationFailureHandler) // 로그인 실패 시 맞춤형 한글 에러 메시지 핸들러 연결
                        .permitAll()                             // 로그인 페이지 및 실패 페이지는 누구나 접근 허용
                )

                // ==============================================================================
                // 3. 로그아웃(Logout) 설정
                // ==============================================================================
                .logout(logout -> logout
                        .logoutUrl("/members/logout")            // 로그아웃을 처리할 요청 URL
                        .logoutSuccessUrl("/")                   // 로그아웃 성공 후 이동할 기본 페이지
                        .invalidateHttpSession(true)             // 로그아웃 시 현재 세션을 완전히 무효화(삭제)
                        .deleteCookies("JSESSIONID")             // 브라우저의 세션 쿠키 삭제
                        .permitAll()                             // 로그아웃 요청은 누구나 접근 허용
                )

                // ==============================================================================
                // 4. 예외 처리(Exception Handling) 설정
                // ==============================================================================
                .exceptionHandling(exception -> exception
                        // 위에서 등록한 CustomAuthenticationEntryPoint 빈 연결
                        .authenticationEntryPoint(authenticationEntryPoint())
                );

        // 완성된 HttpSecurity 보안 설정을 기반으로 SecurityFilterChain 생성 후 반환
        return http.build();
    } //end filterChain
}
