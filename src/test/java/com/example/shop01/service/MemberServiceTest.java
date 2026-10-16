package com.example.shop01.service;

import com.example.shop01.domain.Member;
import com.example.shop01.dto.MemberFormDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [회원 서비스 단위/통합 테스트 - MemberServiceTest]
 */
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MemberFormDto createMemberFormDto(String email) {
        MemberFormDto dto = new MemberFormDto();
        dto.setEmail(email);
        dto.setName("테스트사용자");
        dto.setAddress("서울특별시 강남구 테헤란로 123");
        dto.setPassword("12345678");
        return dto;
    }

    @Test
    @DisplayName("회원가입 정상 등록 테스트")
    void saveMemberTest() {
        MemberFormDto memberFormDto = createMemberFormDto("test_user_01@rehmia.com");
        Member member = memberService.registerMember(memberFormDto);

        assertNotNull(member.getId());
        assertEquals(memberFormDto.getEmail(), member.getEmail());
        assertEquals(memberFormDto.getName(), member.getName());
        assertTrue(passwordEncoder.matches("12345678", member.getPassword()));
    }

    @Test
    @DisplayName("중복 회원가입 시 예외 발생 검증 테스트")
    void saveDuplicateMemberTest() {
        MemberFormDto member1 = createMemberFormDto("duplicate@rehmia.com");
        MemberFormDto member2 = createMemberFormDto("duplicate@rehmia.com");

        memberService.registerMember(member1);

        Throwable exception = assertThrows(IllegalStateException.class, () -> {
            memberService.registerMember(member2);
        });

        assertEquals("이미 가입된 회원입니다.", exception.getMessage());
    }
}
