package io.sanchit.socialbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SocialBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialBookApplication.class, args);
	}

}
