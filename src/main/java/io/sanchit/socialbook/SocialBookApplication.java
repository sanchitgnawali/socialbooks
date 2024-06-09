package io.sanchit.socialbook;

import io.sanchit.socialbook.role.Role;
import io.sanchit.socialbook.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SocialBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialBookApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository) {

        return args -> {
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(Role.builder().name("USER").build());
            }

        };
    }

}
