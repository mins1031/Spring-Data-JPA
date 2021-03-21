package study.datajap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class StudyjpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyjpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider(){
		return () -> Optional.of(UUID.randomUUID().toString());
		//등록자, 수정자 값 받아오는 로직이라 재대로하면 스프링 시큐리티 컨택스트
		//의 세션에서 사용자 id값 가져와서 넣어주는등의 로직이 필요
	}

}
