package com.example.shop01.repository;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.domain.Item;
import com.example.shop01.domain.QItem;
import com.example.shop01.domain.QItemImg;
import com.example.shop01.dto.ItemSearchDto;
import com.example.shop01.dto.MainItemDto;
import com.example.shop01.dto.QMainItemDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * [사용자 정의 Querydsl 구현 클래스 - ItemRepositoryCustomImpl]
 *
 * ItemRepositoryCustom 인터페이스를 구현하며,
 * JPAQueryFactory를 활용하여 타입 안정적(Type-Safe)이고 유연한 동적 쿼리를 실행합니다.
 */
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * EntityManager를 주입받아 JPAQueryFactory를 초기화하는 생성자
     *
     * @param em JPA 엔티티 매니저
     */
    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    } //end constructor

    /**
     * 상품 판매 상태(SELL / SOLD_OUT) 일치 조건 생성 메서드
     *
     * @param searchSellStatus 검색할 판매 상태
     * @return BooleanExpression 조건식 (null인 경우 전체 조회)
     */
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    } //end searchSellStatusEq

    /**
     * 상품 등록 기간(1d, 1w, 1m, 6m) 기준 조건 생성 메서드
     *
     * @param searchDateType 기간 검색 유형 ("all", "1d", "1w", "1m", "6m")
     * @return BooleanExpression 조건식
     */
    private BooleanExpression regDtsAfter(String searchDateType) {
        if (!StringUtils.hasText(searchDateType) || "all".equalsIgnoreCase(searchDateType)) {
            return null;
        }

        LocalDateTime dateTime = LocalDateTime.now();

        switch (searchDateType.toLowerCase()) {
            case "1d":
                dateTime = dateTime.minusDays(1);
                break;
            case "1w":
                dateTime = dateTime.minusWeeks(1);
                break;
            case "1m":
                dateTime = dateTime.minusMonths(1);
                break;
            case "6m":
                dateTime = dateTime.minusMonths(6);
                break;
            default:
                return null;
        }

        return QItem.item.regTime.after(dateTime);
    } //end regDtsAfter

    /**
     * 검색 대상 유형("itemName", "createdBy", "itemDetail" 등)에 따른 키워드 LIKE 검색 조건 생성 메서드
     *
     * @param searchBy    검색 대상 필드 유형
     * @param searchQuery 검색어
     * @return BooleanExpression 조건식
     */
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (!StringUtils.hasText(searchQuery)) {
            return null;
        }

        String keyword = searchQuery.trim();

        if ("createdBy".equals(searchBy)) {
            return QItem.item.createdBy.like("%" + keyword + "%");
        }

        if ("itemDetail".equals(searchBy)) {
            return QItem.item.itemDetail.like("%" + keyword + "%");
        }

        // 기본값: 상품명(itemName) 검색
        return QItem.item.itemName.like("%" + keyword + "%");
    } //end searchByLike

    /**
     * 메인 화면 전용 상품명 검색어 LIKE 조건 생성 메서드
     *
     * @param searchQuery 메인 화면 검색어
     * @return BooleanExpression 조건식
     */
    private BooleanExpression itemNameLike(String searchQuery) {
        return !StringUtils.hasText(searchQuery) ? null : QItem.item.itemName.like("%" + searchQuery.trim() + "%");
    } //end itemNameLike

    /**
     * [관리자 상품 목록 페이징 조회]
     * - 상품 등록일자, 판매상태, 검색조건 및 검색어를 동적으로 조합하여 페이징 조회합니다.
     * - PageableExecutionUtils를 사용하여 카운트 쿼리를 최적화합니다.
     */
    @Override
    public Page<Item> getItemAdminPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;

        // 1. 페이징 데이터 목록 조회
        List<Item> content = queryFactory
                .selectFrom(item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                )
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 전체 카운트 쿼리 (PageableExecutionUtils를 통해 필요 시에만 실행)
        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> {
            Long count = countQuery.fetchOne();
            return count != null ? count : 0L;
        });
    } //end getItemAdminPage

    /**
     * [메인 화면 상품 목록 페이징 조회]
     * - 대표 이미지(repImgYn == "Y")를 조인하고, QMainItemDto 프로젝션을 통해 화면에 필요한 데이터만 직접 조회합니다.
     * - PageableExecutionUtils를 사용하여 카운트 쿼리를 최적화합니다.
     */
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        // 1. 메인 상품 DTO 목록 조회 (QueryProjection 활용)
        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemName,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price
                        )
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 전체 카운트 쿼리 (PageableExecutionUtils를 통해 필요 시에만 실행)
        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNameLike(itemSearchDto.getSearchQuery()));

        return PageableExecutionUtils.getPage(content, pageable, () -> {
            Long count = countQuery.fetchOne();
            return count != null ? count : 0L;
        });
    } //end getMainItemPage

} //end class ItemRepositoryCustomImpl
