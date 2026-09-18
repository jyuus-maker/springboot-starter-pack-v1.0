package com.example.shop01.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * [JPA Auditing - 등록일/수정일/등록자/수정자 공통 엔티티]
 *
 * 모든 엔티티에서 공통으로 사용되는 등록 시간(regTime), 수정 시간(updateTime),
 * 등록자(createdBy), 수정자(modifiedBy)를 Spring Data JPA의 Auditing 기능을 통해 자동으로 관리해주는 추상 부모 클래스입니다.
 */
@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    /**
     * 엔티티가 생성되어 저장될 때의 시간 (최초 등록 시점)
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regTime;

    /**
     * 엔티티의 값을 변경(수정)할 때마다 갱신되는 시간
     */
    @LastModifiedDate
    private LocalDateTime updateTime;

    /**
     * 엔티티를 생성하여 등록한 사용자 계정/식별자
     */
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    /**
     * 엔티티를 마지막으로 수정한 사용자 계정/식별자
     */
    @LastModifiedBy
    private String modifiedBy;

} //end class BaseEntity
