package com.example.shop01.repository;

import com.example.shop01.domain.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상품 이미지(ItemImg) 엔티티에 대한 데이터 액세스를 처리하는 Repository 인터페이스
 */
@Repository
public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    /**
     * 특정 상품의 모든 이미지 목록을 ID 순서대로 조회
     *
     * @param itemId 상품 ID
     * @return 상품 이미지 리스트
     */
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    /**
     * 특정 상품의 대표 이미지 조회
     *
     * @param itemId   상품 ID
     * @param repImgYn 대표 이미지 여부 ("Y")
     * @return 대표 이미지 엔티티
     */
    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);

} //end interface ItemImgRepository
