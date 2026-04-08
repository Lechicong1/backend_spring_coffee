package com.example.COFFEEHOUSE.Utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CommonUtils {
    public static Long getIdUserFromToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = jwt.getClaim("id");
        return userId;
    }
    public static String generateOrderCode() {
        Random random = new Random();
        int randomNum = 10000 + random.nextInt(90000);
        return "ORD" + randomNum;
    }

}
