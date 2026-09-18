package com.example.shop01.controller;

import com.example.shop01.dto.ItemSearchDto;
import com.example.shop01.dto.MainItemDto;
import com.example.shop01.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

/**
 * [쇼핑몰 메인 화면 컨트롤러 - MainController]
 */
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    /**
     * 메인 페이지 매핑
     * - 검색어 조건(itemSearchDto) 및 페이징 정보를 받아 메인 상품 목록을 조회하고 뷰에 전달
     *
     * @param itemSearchDto 검색 조건 DTO (검색어 등)
     * @param page          조회할 페이지 번호 (Optional)
     * @param model         스프링 UI 모델
     * @return index.html 메인 템플릿
     */
    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        // 한 페이지당 8개 상품 표시
        Pageable pageable = PageRequest.of(page.orElse(0), 8);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "index";
    } //end main

} //end class MainController
