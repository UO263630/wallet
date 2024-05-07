package com.wallet.Users.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreation {

    @Schema(description = "name", example = "Max")
    private String name;

    @Schema(description = "email", example = "user@email.com")
    private String email;

    @Schema(description = "password", example = "password")
    private String password;

    @Schema(description = "type", example = "email")
    private String type;

}
