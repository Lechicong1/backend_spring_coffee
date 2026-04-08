package com.example.COFFEEHOUSE.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VietQrUtils {
    // Thông tin tài khoản ngân hàng của bạn
    public static final String BANK_ID = "TP";
    public static final String ACCOUNT_NO = "88260305888";
    public static final String ACCOUNT_NAME = "NGUYEN MANH DUC";

    public static String extractOrderCode(String description) {
        if (description == null || description.isEmpty()) return null;
        Pattern pattern = Pattern.compile("(ORD[a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(description);
        return matcher.find() ? matcher.group(1).toUpperCase() : null;
    }
}