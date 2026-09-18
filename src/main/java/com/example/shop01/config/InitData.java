package com.example.shop01.config;

import com.example.shop01.constant.ItemSellStatus;
import com.example.shop01.constant.Role;
import com.example.shop01.domain.Item;
import com.example.shop01.domain.ItemImg;
import com.example.shop01.domain.Member;
import com.example.shop01.repository.ItemImgRepository;
import com.example.shop01.repository.ItemRepository;
import com.example.shop01.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * [쇼핑몰 초기 샘플 데이터 자동 생성기 - InitData]
 * - 애플리케이션 기동 시 테스트용 관리자 계정, 일반 회원 계정, 기본 상품 8종 및 대표 이미지를 DB에 자동 등록합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitData implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. 관리자 계정 생성 (admin@shop01.com / 1234)
        if (memberRepository.findByEmail("admin@shop01.com").isEmpty()) {
            Member admin = new Member();
            admin.setName("관리자");
            admin.setEmail("admin@shop01.com");
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setAddress("서울시 강남구 테헤란로 123 관리자빌딩");
            admin.setRole(Role.ADMIN);
            memberRepository.save(admin);
            log.info(">> [InitData] 관리자 계정 생성 완료: admin@shop01.com / 1234 (ROLE_ADMIN)");
        }

        // 2. 일반 회원 계정 생성 (user@shop01.com / 1234)
        if (memberRepository.findByEmail("user@shop01.com").isEmpty()) {
            Member user = new Member();
            user.setName("홍길동");
            user.setEmail("user@shop01.com");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setAddress("서울시 서초구 서초대로 456");
            user.setRole(Role.USER);
            memberRepository.save(user);
            log.info(">> [InitData] 일반 회원 계정 생성 완료: user@shop01.com / 1234 (ROLE_USER)");
        }

        // 3. 기본 샘플 상품 및 대표 이미지 등록 (상품이 하나도 없을 경우)
        if (itemRepository.count() == 0) {
            createSampleItem(
                    "클래식 빈티지 가죽 크로스백",
                    89000,
                    100,
                    "장인의 정교한 스티칭과 천연 소가죽으로 완성된 타임리스 디자인 크로스백입니다.",
                    "https://cdn.pixabay.com/photo/2016/11/23/18/12/bag-1854148_1280.jpg"
            );

            createSampleItem(
                    "무선 노이즈캔슬링 헤드폰",
                    189000,
                    50,
                    "최상급 하이파이 음질과 완벽한 액티브 노이즈 캔슬링을 선사하는 프리미엄 헤드폰입니다.",
                    "https://cdn.pixabay.com/photo/2016/11/29/09/08/headphones-1868612_1280.jpg"
            );

            createSampleItem(
                    "스마트 피트니스 워치 프로",
                    145000,
                    80,
                    "실시간 심박수, 수면 패턴 분석, 방수 기능을 갖춘 올인원 스마트 트래커입니다.",
                    "https://cdn.pixabay.com/photo/2017/08/01/11/48/blue-2564660_1280.jpg"
            );

            createSampleItem(
                    "어반 모던 데일리 스니커즈",
                    79000,
                    120,
                    "가볍고 편안한 쿠셔닝으로 하루 종일 발이 편안한 미니멀 디자인 스니커즈입니다.",
                    "https://cdn.pixabay.com/photo/2016/11/19/18/06/feet-1840619_1280.jpg"
            );

            createSampleItem(
                    "클래식 UV400 블랙 선글라스",
                    52000,
                    65,
                    "자외선을 99.9% 차단하는 편광 렌즈와 가벼운 프레임의 데일리 선글라스입니다.",
                    "https://cdn.pixabay.com/photo/2017/08/06/12/06/people-2591874_1280.jpg"
            );

            createSampleItem(
                    "방수 비즈니스 백팩 20L",
                    68000,
                    40,
                    "15.6인치 노트북 전용 수납공간과 생활 방수 패브릭을 적용한 스마트 백팩입니다.",
                    "https://cdn.pixabay.com/photo/2015/08/25/11/50/shopping-906722_1280.jpg"
            );

            createSampleItem(
                    "스테인리스 진공 보온 텀블러 500ml",
                    28000,
                    150,
                    "24시간 보온/보냉 유지 및 304 최고급 스테인리스 스틸 소재의 친환경 텀블러입니다.",
                    "https://cdn.pixabay.com/photo/2015/09/05/20/07/coffee-924976_1280.jpg"
            );

            createSampleItem(
                    "레트로 기계식 블루투스 키보드",
                    119000,
                    30,
                    "타자기 감성의 쫀득한 타건감과 멀티 디바이스 페어링을 지원하는 감성 키보드입니다.",
                    "https://cdn.pixabay.com/photo/2016/11/29/08/42/desk-1868494_1280.jpg"
            );

            log.info(">> [InitData] 기본 샘플 상품 8종 및 대표 이미지 등록 완료");
        }
    } //end run

    private void createSampleItem(String name, int price, int stock, String detail, String imgUrl) {
        Item item = new Item();
        item.setItemName(name);
        item.setPrice(price);
        item.setStockNumber(stock);
        item.setItemDetail(detail);
        item.setItemSellStatus(ItemSellStatus.SELL);
        itemRepository.save(item);

        ItemImg itemImg = new ItemImg();
        itemImg.setItem(item);
        itemImg.setImgName("sample_" + item.getId() + ".jpg");
        itemImg.setOriImgName("sample_" + item.getId() + ".jpg");
        itemImg.setImgUrl(imgUrl);
        itemImg.setRepImgYn("Y");
        itemImgRepository.save(itemImg);
    } //end createSampleItem

} //end class InitData
