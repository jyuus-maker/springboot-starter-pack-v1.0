package com.example.shop01.dto;

import com.example.shop01.constant.ItemSellStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 상품 관리 및 검색 조건 데이터를 전달하기 위한 Data Transfer Object (DTO)
 * - 상품 등록 기간, 판매 상태, 검색 유형(상품명/등록자), 검색어 등의 필터링 조건을 포함
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchDto {

    /**
     * 상품 등록 기간 검색 조건
     * - all: 전체 기간
     * - 1d: 최근 1일
     * - 1w: 최근 1주
     * - 1m: 최근 1개월
     * - 6m: 최근 6개월
     */
    @Builder.Default
    private String searchDateType = "all";

    /**
     * 상품 판매 상태 검색 조건
     * - null: 전체
     * - SELL: 판매중
     * - SOLD_OUT: 품절
     */
    private ItemSellStatus searchSellStatus;

    /**
     * 검색 대상 유형
     * - itemName: 상품명 기준 검색
     * - createdBy: 상품 등록자 기준 검색
     */
    private String searchBy;

    /**
     * 조회할 검색어 (검색 키워드)
     */
    @Builder.Default
    private String searchQuery = "";

} //end class ItemSearchDto
