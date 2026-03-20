package com.example.COFFEEHOUSE.DTO.Response;

import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResp {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Long roleId;
    private Long points;

}
