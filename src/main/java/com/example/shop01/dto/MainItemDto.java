package com.example.shop01.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 메인 페이지(홈 화면)에서 상품 목록을 카드 형태로 출력하기 위한 Data Transfer Object (DTO)
 * - QueryDSL의 @QueryProjection을 활용하여 DB에서 필요한 컬럼(상품정보 + 대표이미지)만 직접 조회
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
public class MainItemDto {

    private Long id; // 상품 코드 (상세 페이지 이동 링크용)

    private String itemName; // 상품명

    private String itemDetail; // 상품 설명 요약

    private String imgUrl; // 상품 대표 이미지 경로 (썸네일 URL)

    private Integer price; // 상품 가격

    /**
     * QueryDSL 프로젝션 결과 반환을 위한 생성자
     * - @QueryProjection 어노테이션을 통해 QMainItemDto 클래스를 자동 생성
     */
    @QueryProjection
    public MainItemDto(Long id, String itemName, String itemDetail, String imgUrl, Integer price) {
        this.id = id;
        this.itemName = itemName;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    } //end constructor

} //end class MainItemDto
