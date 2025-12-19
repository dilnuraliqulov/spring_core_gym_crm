package com.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after successful registration")
public class RegistrationResponse {

    @Schema(description = "Generated username", example = "John.Doe")
    private String username;

    @Schema(description = "Generated password")
    private String password;
}

