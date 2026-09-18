package com.example.shop01.controller;

import com.example.shop01.dto.MemberFormDto;
import com.example.shop01.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입 페이지 이동
     */
    @GetMapping("/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    } //end memberForm

    /**
     * 회원 가입 요청 처리
     */
    @PostMapping("/new")
    public String memberNew(@Valid @ModelAttribute("memberFormDto") MemberFormDto memberFormDto,
                            BindingResult bindingResult,
                            Model model) {
        // 비밀번호 확인 일치 여부 검증
        validatePassword(memberFormDto, bindingResult);

        // 폼 유효성 검증 실패 시 다시 폼 화면으로 이동
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try {
            memberService.registerMember(memberFormDto);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    } //end memberNew

    /**
     * 로그인 페이지 이동
     */
    @GetMapping("/login")
    public String loginForm() {
        return "member/memberLoginForm";
    } //end loginForm

    /**
     * 로그인 실패 시 에러 처리 및 로그인 페이지 이동
     */
    @GetMapping("/login/error")
    public String loginError(@RequestParam(value = "errorMessage", required = false) String errorMessage,
                             Model model) {
        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
            model.addAttribute("loginErrorMsg", errorMessage);
        } else {
            model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        }
        return "member/memberLoginForm";
    } //end loginError

    /**
     * 마이페이지 화면 이동 (로그인한 사용자 정보 전달)
     */
    @GetMapping(value = {"/mypage", "/myPage"})
    public String myPage(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("email", principal.getName());
        }
        return "member/myPage";
    } //end myPage

    /**
     * 비밀번호와 비밀번호 확인 일치 여부 검증 및 오류 처리
     */
    private void validatePassword(MemberFormDto memberFormDto, BindingResult bindingResult) {
        if (memberFormDto.getPassword() != null && memberFormDto.getPasswordConfirm() != null) {
            if (!memberFormDto.getPassword().equals(memberFormDto.getPasswordConfirm())) {
                bindingResult.rejectValue("passwordConfirm", "passwordInconsistent", "비밀번호가 일치하지 않습니다.");
            }
        }
    } //end validatePassword
}
