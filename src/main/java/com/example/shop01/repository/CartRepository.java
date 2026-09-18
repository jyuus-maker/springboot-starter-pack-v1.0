package com.example.shop01.repository;

import com.example.shop01.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * [장바구니 Repository - CartRepository]
 * - 회원의 장바구니 엔티티(Cart)에 대한 데이터 접근을 처리하는 인터페이스
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 특정 회원 ID를 통해 해당 회원의 장바구니 조회
     *
     * @param memberId 회원 ID
     * @return 회원의 장바구니 엔티티 (Optional)
     */
    Optional<Cart> findByMemberId(Long memberId);

} //end interface CartRepository
