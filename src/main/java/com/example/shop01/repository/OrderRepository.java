package com.example.shop01.repository;

import com.example.shop01.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * [주문 Repository - OrderRepository]
 * - 회원의 주문 내역(Order)에 대한 데이터 조회를 담당하는 인터페이스
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 특정 회원의 주문 목록을 페이징하여 조회
     *
     * @param email    주문자 회원 이메일
     * @param pageable 페이징 정보
     * @return 주문 목록 (Order)
     */
    @Query("SELECT o FROM Order o " +
           "WHERE o.member.email = :email " +
           "ORDER BY o.orderDate DESC")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    /**
     * 특정 회원의 전체 주문 건수 조회
     *
     * @param email 주문자 회원 이메일
     * @return 총 주문 건수
     */
    @Query("SELECT count(o) FROM Order o " +
           "WHERE o.member.email = :email")
    Long countOrder(@Param("email") String email);

} //end interface OrderRepository
