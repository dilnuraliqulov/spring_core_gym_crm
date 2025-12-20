package com.gymcrm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for changing password")
public class ChangePasswordRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username", required = true, example = "John.Doe")
    private String username;

    @NotBlank(message = "Old password is required")
    @Schema(description = "Current password", required = true)
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    @Schema(description = "New password (minimum 8 characters)", required = true)
    private String newPassword;
}

