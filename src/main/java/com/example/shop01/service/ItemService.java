package com.example.shop01.service;

import com.example.shop01.domain.Item;
import com.example.shop01.domain.ItemImg;
import com.example.shop01.dto.ItemFormDto;
import com.example.shop01.dto.ItemImgDto;
import com.example.shop01.dto.ItemSearchDto;
import com.example.shop01.dto.MainItemDto;
import com.example.shop01.repository.ItemImgRepository;
import com.example.shop01.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 상품 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    /**
     * 상품 등록 (상품 정보 + 상품 이미지 파일 목록 일괄 등록)
     *
     * @param itemFormDto     상품 등록 폼 데이터
     * @param itemImgFileList 업로드된 상품 이미지 파일 리스트
     * @return 저장된 상품의 ID
     * @throws Exception 파일 저장 중 발생할 수 있는 예외
     */
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 1. 상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        // 2. 이미지 등록을 위한 for문
        if (itemImgFileList != null) {
            for (int i = 0; i < itemImgFileList.size(); i++) {
                ItemImg itemImg = new ItemImg();
                itemImg.setItem(item);

                // 첫 번째 이미지는 대표 이미지로 설정
                if (i == 0) {
                    itemImg.setRepImgYn("Y");
                } else {
                    itemImg.setRepImgYn("N");
                }

                // 개별 이미지 파일 저장 처리
                itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
            }
        }

        return item.getId();
    } //end saveItem

    /**
     * 상품 등록 (단순 ItemFormDto만 전달 시 호출되는 편의 메서드)
     *
     * @param itemFormDto 상품 등록 폼 데이터
     * @return 저장된 상품의 ID
     */
    public Long saveItem(ItemFormDto itemFormDto) {
        Item item = itemFormDto.createItem();
        itemRepository.save(item);
        return item.getId();
    } //end saveItem

    /**
     * 상품 등록 (Item 엔티티 직접 저장)
     *
     * @param item 저장할 상품 엔티티
     * @return 저장된 상품 엔티티
     */
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    } //end saveItem

    /**
     * 상품 상세 정보 조회 (상품 정보 + 기존 이미지 목록 포함하여 ItemFormDto 반환)
     *
     * @param itemId 조회할 상품의 ID
     * @return 상품 폼 DTO
     */
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        // 1. 해당 상품의 이미지 목록 조회
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        List<Long> itemImgIds = new ArrayList<>();

        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
            itemImgIds.add(itemImg.getId());
        }

        // 2. 상품 엔티티 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다. (ID: " + itemId + ")"));

        // 3. ItemFormDto 변환 및 이미지 정보 설정
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        itemFormDto.setItemImgIds(itemImgIds);

        return itemFormDto;
    } //end getItemDtl

    /**
     * 상품 정보 및 이미지 수정 (더티 체킹 및 이미지 교체/업데이트)
     *
     * @param itemFormDto     수정할 상품 정보 DTO
     * @param itemImgFileList 새롭게 업로드된 이미지 파일 리스트
     * @return 수정된 상품 ID
     * @throws Exception 파일 수정 중 발생할 수 있는 예외
     */
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 1. 상품 엔티티 수정 (더티 체킹에 의해 트랜잭션 커밋 시 자동 UPDATE)
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("수정할 상품이 존재하지 않습니다. (ID: " + itemFormDto.getId() + ")"));
        item.updateItem(itemFormDto);

        // 2. 상품 이미지 수정
        List<Long> itemImgIds = itemFormDto.getItemImgIds();
        if (itemImgFileList != null && !itemImgFileList.isEmpty()) {
            for (int i = 0; i < itemImgFileList.size(); i++) {
                MultipartFile itemImgFile = itemImgFileList.get(i);
                if (itemImgIds != null && i < itemImgIds.size()) {
                    Long itemImgId = itemImgIds.get(i);
                    itemImgService.updateItemImg(itemImgId, itemImgFile);
                } else if (itemImgFile != null && !itemImgFile.isEmpty()) {
                    // 추가 등록된 이미지인 경우 신규 생성
                    ItemImg itemImg = new ItemImg();
                    itemImg.setItem(item);
                    itemImg.setRepImgYn("N");
                    itemImgService.saveItemImg(itemImg, itemImgFile);
                }
            }
        }

        return item.getId();
    } //end updateItem

    /**
     * 상품 정보 수정 (단순 DTO 오버로딩)
     *
     * @param itemFormDto 수정할 정보가 담긴 DTO
     * @return 수정된 상품의 ID
     */
    public Long updateItem(ItemFormDto itemFormDto) {
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("수정할 상품이 존재하지 않습니다. (ID: " + itemFormDto.getId() + ")"));
        item.updateItem(itemFormDto);
        return item.getId();
    } //end updateItem

    /**
     * 상품 단건 엔티티 조회
     *
     * @param itemId 조회할 상품 ID
     * @return Item 엔티티
     */
    @Transactional(readOnly = true)
    public Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다. (ID: " + itemId + ")"));
    } //end findItemById

    /**
     * 전체 상품 목록 조회
     *
     * @return 전체 Item 목록
     */
    @Transactional(readOnly = true)
    public List<Item> findItemList() {
        return itemRepository.findAll();
    } //end findItemList

    /**
     * 관리자 상품 관리 화면용 다중 조건 검색 및 페이징 조회
     *
     * @param itemSearchDto 상품 검색 조건
     * @param pageable      페이징 정보
     * @return 상품 페이징 객체 (Page<Item>)
     */
    @Transactional(readOnly = true)
    public Page<Item> getItemAdminPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getItemAdminPage(itemSearchDto, pageable);
    } //end getItemAdminPage

    /**
     * 메인 화면용 상품 목록 조회 (대표 이미지 조인 및 검색어 필터링, 페이징)
     *
     * @param itemSearchDto 상품 검색 조건
     * @param pageable      페이징 정보
     * @return 메인 페이지 상품 DTO 페이징 객체 (Page<MainItemDto>)
     */
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    } //end getMainItemPage

    /**
     * 상품 삭제
     *
     * @param itemId 삭제할 상품 ID
     */
    public void deleteItem(Long itemId) {
        Item item = findItemById(itemId);
        itemRepository.delete(item);
    } //end deleteItem

} //end class ItemService
