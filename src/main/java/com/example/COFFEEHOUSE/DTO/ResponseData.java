package com.example.COFFEEHOUSE.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response wrapper chuẩn cho tất cả API responses")
public class ResponseData {

    private Object data;
    private String message;
    private boolean success = false;
}
