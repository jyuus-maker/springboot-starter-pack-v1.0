package com.example.shop01.domain;

import com.example.shop01.constant.Role;
import com.example.shop01.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * MemberFormDto와 PasswordEncoder를 전달받아
     * 비밀번호를 안전하게 암호화한 후 Member 엔티티를 생성하는 팩토리 메서드
     */
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());

        // 비밀번호 보안 암호화 처리 (BCrypt 해싱)
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);

        member.setRole(Role.USER); // 기본 사용자 권한 (Role.ADMIN 등)
        return member;
    } //end createMember
}
