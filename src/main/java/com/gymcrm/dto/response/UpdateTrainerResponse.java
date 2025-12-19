package com.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Updated trainer profile response")
public class UpdateTrainerResponse {

    @Schema(description = "Username", example = "Jane.Smith")
    private String username;

    @Schema(description = "First name", example = "Jane")
    private String firstName;

    @Schema(description = "Last name", example = "Smith")
    private String lastName;

    @Schema(description = "Specialization (training type)", example = "Fitness")
    private String specialization;

    @Schema(description = "Whether the trainer is active", example = "true")
    private Boolean isActive;

    @Schema(description = "List of assigned trainees")
    private List<TrainerProfileResponse.TraineeInfo> trainees;
}

