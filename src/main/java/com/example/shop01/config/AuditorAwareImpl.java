package com.example.shop01.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * [JPA Auditing - 사용자 정보 주입 구현체]
 * 
 * 엔티티(Entity)가 생성되거나 수정될 때(@CreatedBy, @LastModifiedBy),
 * 현재 로그인한 사용자의 정보를 자동으로 추적하여 등록자/수정자로 기록하기 위한 클래스입니다.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * 현재 요청을 보낸 사용자의 식별자(ID 또는 Email)를 Optional로 반환합니다.
     * 
     * - 로그인 상태: SecurityContext에서 인증(Authentication) 정보를 꺼내 사용자 이름(Email)을 반환
     * - 비로그인/시스템 상태: 인증 객체가 없으므로 빈 문자열("")을 담아 반환 (NPE 방지)
     *
     * @return 현재 로그인한 사용자의 식별자 (Optional)
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        // 1. 현재 스레드의 SecurityContext에서 인증 객체(Authentication) 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";

        // 2. 인증 객체가 존재하고 인증된 상태인 경우 사용자 식별자(Name/Email) 추출
        if (authentication != null) {
            userId = authentication.getName();
        }

        // 3. Optional로 감싸서 반환하여 JPA Auditing이 @CreatedBy / @LastModifiedBy에 자동 바인딩
        return Optional.of(userId);
    } //end getCurrentAuditor
}
