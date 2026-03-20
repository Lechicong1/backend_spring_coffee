package com.example.COFFEEHOUSE.Utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {
    public static Long getIdUserFromToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = jwt.getClaim("id");
        return userId;
    }

}
