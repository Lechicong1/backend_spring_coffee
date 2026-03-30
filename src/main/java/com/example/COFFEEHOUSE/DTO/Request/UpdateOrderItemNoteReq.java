package com.example.COFFEEHOUSE.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderItemNoteReq {
    @NotNull
    private Long orderItemId;

    @NotBlank
    private String note;
}

