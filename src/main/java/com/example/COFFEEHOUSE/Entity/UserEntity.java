package com.example.COFFEEHOUSE.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String address;
    private Long roleId;
    private Long points;

}
