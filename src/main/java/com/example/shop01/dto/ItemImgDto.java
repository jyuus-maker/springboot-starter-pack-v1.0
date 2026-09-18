package com.example.shop01.dto;

import com.example.shop01.domain.ItemImg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

/**
 * 상품 이미지 정보를 전달하기 위한 Data Transfer Object (DTO)
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemImgDto {

    private Long id; // 상품 이미지 ID

    private String imgName; // 실제 저장된 이미지 파일명

    private String oriImgName; // 원본 이미지 파일명

    private String imgUrl; // 이미지 조회 경로 (URL)

    private String repImgYn; // 대표 이미지 여부 ("Y" / "N")

    private static ModelMapper modelMapper = new ModelMapper();

    /**
     * Entity -> DTO 변환 정적 팩토리 메서드
     *
     * @param itemImg ItemImg 엔티티
     * @return 변환된 ItemImgDto 객체
     */
    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg, ItemImgDto.class);
    } //end of

} //end class ItemImgDto
