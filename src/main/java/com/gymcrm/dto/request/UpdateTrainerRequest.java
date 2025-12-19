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
@Schema(description = "Request for updating trainer profile")
public class UpdateTrainerRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the trainer", required = true, example = "Jane.Smith")
    private String username;

    @NotBlank(message = "First name is required")
    @Schema(description = "First name of the trainer", required = true, example = "Jane")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name of the trainer", required = true, example = "Smith")
    private String lastName;

    @Schema(description = "Specialization ID (read-only, cannot be changed)", example = "1")
    private Long specializationId;

    @NotNull(message = "Is active status is required")
    @Schema(description = "Whether the trainer is active", required = true, example = "true")
    private Boolean isActive;
}

