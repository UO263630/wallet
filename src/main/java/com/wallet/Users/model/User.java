package com.wallet.Users.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Schema(description = "type", example = "email")
    private String type;

    @Schema(description = "name", example = "user@email.com")
    private String email;

    @Schema(description = "password", example = "password")
    private String password;

}
