package com.gymcrm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for updating trainee profile")
public class UpdateTraineeRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the trainee", required = true, example = "John.Doe")
    private String username;

    @NotBlank(message = "First name is required")
    @Schema(description = "First name of the trainee", required = true, example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name of the trainee", required = true, example = "Doe")
    private String lastName;

    @Schema(description = "Date of birth (optional)", example = "1990-01-15")
    private Date dateOfBirth;

    @Schema(description = "Address (optional)", example = "123 Main St, City")
    private String address;

    @NotNull(message = "Is active status is required")
    @Schema(description = "Whether the trainee is active", required = true, example = "true")
    private Boolean isActive;
}

