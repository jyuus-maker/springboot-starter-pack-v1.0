package com.example.shop01.repository;

import com.example.shop01.domain.Item;
import com.example.shop01.dto.ItemSearchDto;
import com.example.shop01.dto.MainItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * [사용자 정의 Querydsl 인터페이스 - ItemRepositoryCustom]
 *
 * Spring Data JPA의 JpaRepository 기본 메서드 외에,
 * Querydsl을 이용한 동적 쿼리 및 다중 조건 검색, 페이징 처리를 수행하기 위한 커스텀 인터페이스입니다.
 */
public interface ItemRepositoryCustom {

    /**
     * 관리자 상품 관리 페이지용 다중 조건 검색 및 페이징 조회
     *
     * @param itemSearchDto 상품 검색 조건 DTO (등록일자, 판매상태, 검색조건, 검색어)
     * @param pageable      페이징 및 정렬 정보
     * @return 조건에 일치하는 상품의 페이징 객체 (Page<Item>)
     */
    Page<Item> getItemAdminPage(ItemSearchDto itemSearchDto, Pageable pageable);

    /**
     * 메인 화면용 상품 목록 조회 (대표 이미지 조인 및 검색어 필터링, 페이징)
     *
     * @param itemSearchDto 메인 검색창 검색 조건 DTO
     * @param pageable      페이징 정보
     * @return 메인 페이지 노출용 MainItemDto 페이징 객체 (Page<MainItemDto>)
     */
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

} //end interface ItemRepositoryCustom
