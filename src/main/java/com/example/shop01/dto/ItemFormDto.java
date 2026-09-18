package com.example.shop01.dto;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.domain.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 상품 등록 및 수정을 위한 폼 데이터 전달 객체 (DTO)
 * - 상품 등록/수정 화면(Form)에서 입력받은 데이터를 Controller 및 Service 계층으로 전달
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFormDto {

    private Long id; // 상품 코드 (수정 시 식별용)

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemName; // 상품명

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price; // 가격

    @NotNull(message = "재고 수량은 필수 입력 값입니다.")
    private Integer stockNumber; // 재고수량

    @NotBlank(message = "상품 상세 설명은 필수 입력 값입니다.")
    private String itemDetail; // 상품 상세설명

    private ItemSellStatus itemSellStatus; // 상품 판매 상태 (SELL, SOLD_OUT)

    // 상품 수정 페이지에서 기존 이미지들을 표시하기 위한 리스트 (상품 등록 시에는 비어있음)
    @Builder.Default
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    // 상품 이미지 아이디들을 저장하는 리스트 (상품 수정 시 어떤 이미지를 수정/유지할지 식별하는 용도)
    @Builder.Default
    private List<Long> itemImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    /**
     * DTO -> Entity 변환 메서드
     *
     * @return Item 엔티티 객체
     */
    public Item createItem() {
        return modelMapper.map(this, Item.class);
    } //end createItem

    /**
     * Entity -> DTO 변환 정적 팩토리 메서드
     *
     * @param item Item 엔티티 객체
     * @return ItemFormDto 객체
     */
    public static ItemFormDto of(Item item) {
        return modelMapper.map(item, ItemFormDto.class);
    } //end of

} //end class ItemFormDto
