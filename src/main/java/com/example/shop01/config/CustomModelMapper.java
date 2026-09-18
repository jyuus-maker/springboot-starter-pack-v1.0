package com.example.shop01.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [ModelMapper 설정 클래스]
 *
 * 서로 다른 두 객체(주로 Entity <-> DTO) 간의 필드 값을 자동으로 매핑(변환)해주는
 * ModelMapper 라이브러리를 스프링 컨테이너의 빈(Bean)으로 등록하는 설정 클래스입니다.
 * 
 * - 사용 이유: 엔티티와 DTO 간의 반복적인 getter/setter 호출 코드를 대폭 줄여 생산성을 높여줍니다.
 */
@Configuration
public class CustomModelMapper {

    /**
     * ModelMapper 인스턴스를 스프링 빈으로 등록
     * 
     * 서비스 계층 등 필요한 곳에서 @Autowired 또는 생성자 주입(@RequiredArgsConstructor)을 통해
     * modelMapper.map(source, Destination.class) 형태로 간편하게 변환 작업을 수행할 수 있습니다.
     *
     * @return 설정된 ModelMapper 인스턴스
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    } //end modelMapper
}
