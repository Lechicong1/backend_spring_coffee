package com.example.COFFEEHOUSE.Config;



import com.example.COFFEEHOUSE.Entity.UserEntity;
import com.example.COFFEEHOUSE.Reposistory.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
@Configuration
public class Createjwt {

        @Value("${spring.security.authentication.jwt.token-validity-in-seconds}")
        private long tokenValidityInSeconds;
        private final JwtEncoder  jwtEncoder;
        private final UserRepo userRepo;

        public String createToken(Authentication authentication) {
            if (authentication == null) {
                throw new IllegalArgumentException("Authentication cannot be null");
            }

            UserEntity user = userRepo.findByUsername(authentication.getName());
            Long userId = user != null ? user.getId() : null;

            Instant now = Instant.now();
            Instant validity = now.plus(tokenValidityInSeconds, ChronoUnit.SECONDS);

            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.replaceFirst("^SCOPE_", "")
                            .replaceFirst("^OAUTH2_", "")
                            .replaceFirst("^ROLE_", ""))
                    .collect(Collectors.toList());

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuedAt(now)
                    .expiresAt(validity)
                    .subject(authentication.getName())
                    .claim("id", userId)
                    .claim("authorities", authorities)
                    .build();

            JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS512).build();
            return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
        }
}
