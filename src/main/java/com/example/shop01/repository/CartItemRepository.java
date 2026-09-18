package com.example.shop01.repository;

import com.example.shop01.domain.CartItem;
import com.example.shop01.dto.CartDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 장바구니에 담긴 상품(CartItem) 엔티티에 대한 데이터 액세스를 처리하는 Repository 인터페이스
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 특정 장바구니에 특정 상품이 이미 담겨있는지 조회하는 쿼리 메서드
     *
     * @param cartId 장바구니 ID (Cart의 식별자)
     * @param itemId 상품 ID (Item의 식별자)
     * @return 장바구니 상품 엔티티 (존재하지 않을 경우 null 반환)
     */
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    /**
     * 특정 장바구니의 모든 상품 목록을 조회하는 쿼리 메서드
     *
     * @param cartId 장바구니 ID
     * @return 해당 장바구니에 속한 장바구니 상품 목록
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * 특정 장바구니의 모든 상품 목록을 최근 담은 순서(ID 역순)로 조회하는 쿼리 메서드
     *
     * @param cartId 장바구니 ID
     * @return 최신순으로 정렬된 장바구니 상품 목록
     */
    List<CartItem> findByCartIdOrderByIdDesc(Long cartId);

    /**
     * 장바구니 페이지용 CartDetailDto 목록을 직접 프로젝션 조회
     *
     * @param cartId 장바구니 ID
     * @return 장바구니 상세 DTO 리스트
     */
    @Query("SELECT new com.example.shop01.dto.CartDetailDto(ci.id, i.itemName, i.price, ci.count, im.imgUrl) " +
           "FROM CartItem ci " +
           "JOIN ci.item i " +
           "JOIN ItemImg im ON im.item.id = i.id " +
           "WHERE ci.cart.id = :cartId " +
           "AND im.repImgYn = 'Y' " +
           "ORDER BY ci.id DESC")
    List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId);

} //end interface CartItemRepository
