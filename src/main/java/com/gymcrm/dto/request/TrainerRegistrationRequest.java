package com.gymcrm.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for trainer registration")
public class TrainerRegistrationRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "First name of the trainer", required = true, example = "Jane")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name of the trainer", required = true, example = "Smith")
    private String lastName;

    @NotNull(message = "Specialization is required")
    @JsonAlias("specialization")
    @Schema(description = "Training type ID for specialization", required = true, example = "1")
    private Long specializationId;
}

