package com.example.shop01.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 상품 이미지 엔티티 (ItemImg)
 * - 상품의 이미지 파일 정보(파일명, 원본명, 경로, 대표이미지 여부 등)를 관리
 */
@Entity
@Table(name = "item_img")
@Getter
@Setter
@ToString(exclude = "item")
@NoArgsConstructor
public class ItemImg extends BaseEntity {

    @Id
    @Column(name = "item_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 이미지 식별자

    private String imgName; // 서버에 실제 저장된 이미지 파일명 (UUID 적용)

    private String oriImgName; // 사용자가 업로드한 원본 이미지 파일명

    private String imgUrl; // 이미지 조회 경로 (URL)

    private String repImgYn; // 대표 이미지 여부 ("Y" / "N")

    // 하나의 상품에 여러 개의 이미지가 등록됨 (N:1 다대일 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * 이미지 정보 업데이트 비즈니스 메서드
     *
     * @param oriImgName 원본 이미지 파일명
     * @param imgName    저장된 이미지 파일명
     * @param imgUrl     이미지 조회 경로
     */
    public void updateItemImg(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    } //end updateItemImg

} //end class ItemImg
