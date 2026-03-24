package com.example.COFFEEHOUSE.DTO.Response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResp {
    private Long id;
    private String name;
    private String description;
}

