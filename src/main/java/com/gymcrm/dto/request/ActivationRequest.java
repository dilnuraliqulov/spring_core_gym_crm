package com.gymcrm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for activating/deactivating a user")
public class ActivationRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the user", required = true, example = "John.Doe")
    private String username;

    @NotNull(message = "Is active status is required")
    @Schema(description = "Whether to activate (true) or deactivate (false)", required = true, example = "true")
    private Boolean isActive;
}

