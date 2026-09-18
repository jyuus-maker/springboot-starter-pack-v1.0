package com.example.shop01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * [JPA Auditing 설정 클래스]
 * - 엔티티의 생성일, 수정일 및 생성자, 수정자를 자동으로 관리하도록 활성화
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    /**
     * 등록자 및 수정자 정보를 제공하는 AuditorAware 빈 등록
     *
     * @return AuditorAwareImpl 인스턴스
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    } //end auditorProvider

} //end class AuditConfig
