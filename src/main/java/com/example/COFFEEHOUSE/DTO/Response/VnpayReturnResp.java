package com.example.COFFEEHOUSE.DTO.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VnpayReturnResp {
    private boolean success;
    private String orderCode;
    private String message;
    private String transactionNo;
    private String responseCode;
}
