package com.example.COFFEEHOUSE.Config;



import com.example.COFFEEHOUSE.Entity.RoleEntity;
import com.example.COFFEEHOUSE.Entity.UserEntity;
import com.example.COFFEEHOUSE.Enums.ROLE;
import com.example.COFFEEHOUSE.Reposistory.RoleRepo;
import com.example.COFFEEHOUSE.Reposistory.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    private final RoleRepo roleRepo;

    @Bean
    ApplicationRunner runner() {
        return args -> {

            for (ROLE roleType : ROLE.values()) {
                if (roleRepo.findByName(roleType.name()) == null) {
                    RoleEntity role = RoleEntity.builder()
                            .name(roleType.name())
                            .build();
                    roleRepo.save(role);
                }
            }
            if (userRepo.findByUsername("admin") == null) {
                RoleEntity adminRole = (RoleEntity) roleRepo.findByName(ROLE.ADMIN.name());
                UserEntity user = UserEntity.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin")   )
                        .email("Lechicong20055@gmail.com")
                        .fullName("Admin")
                        .roleId(adminRole.getId()).build();
                userRepo.save(user);
            }
        };
    }
}
