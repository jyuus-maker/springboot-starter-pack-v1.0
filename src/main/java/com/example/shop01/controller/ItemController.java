package com.example.shop01.controller;

import com.example.shop01.domain.Item;
import com.example.shop01.dto.ItemFormDto;
import com.example.shop01.dto.ItemSearchDto;
import com.example.shop01.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * [상품 관리 및 상세 조회 컨트롤러 - ItemController]
 */
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 관리자 - 상품 등록 페이지 이동
     */
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    } //end itemForm

    /**
     * 관리자 - 상품 등록 처리 (상품 정보 + 다중 이미지 업로드)
     */
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam(value = "itemImgFile", required = false) List<MultipartFile> itemImgFileList) {
        // 1. 유효성 검사 에러 처리
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        // 2. 첫 번째 대표 이미지가 첨부되었는지 검증 (신규 등록 시 필수)
        if (itemImgFileList == null || itemImgFileList.isEmpty() || itemImgFileList.get(0).isEmpty()) {
            model.addAttribute("errorMessage", "첫 번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        // 3. 상품 및 이미지 저장 처리
        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 오류가 발생하였습니다: " + e.getMessage());
            return "item/itemForm";
        }

        return "redirect:/admin/items";
    } //end itemNew

    /**
     * 관리자 - 상품 수정 페이지 이동
     */
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtlAdmin(@PathVariable("itemId") Long itemId, Model model) {
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    } //end itemDtlAdmin

    /**
     * 관리자 - 상품 수정 처리 (상품 정보 + 변경된 이미지 파일 업로드)
     */
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam(value = "itemImgFile", required = false) List<MultipartFile> itemImgFileList,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 오류가 발생하였습니다: " + e.getMessage());
            return "item/itemForm";
        }

        return "redirect:/admin/items";
    } //end itemUpdate

    /**
     * 관리자 - 상품 관리 목록 및 검색 페이지 이동
     */
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.orElse(0), 10);
        Page<Item> items = itemService.getItemAdminPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    } //end itemManage

    /**
     * 사용자 - 상품 상세 보기 페이지 이동
     */
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId) {
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("item", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            return "redirect:/";
        }

        return "item/itemDtl";
    } //end itemDtl

} //end class ItemController
