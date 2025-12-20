package com.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer profile response")
public class TrainerProfileResponse {

    @Schema(description = "First name", example = "Jane")
    private String firstName;

    @Schema(description = "Last name", example = "Smith")
    private String lastName;

    @Schema(description = "Specialization (training type)", example = "Fitness")
    private String specialization;

    @Schema(description = "Whether the trainer is active", example = "true")
    private Boolean isActive;

    @Schema(description = "List of assigned trainees")
    private List<TraineeInfo> trainees;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Basic trainee information")
    public static class TraineeInfo {
        @Schema(description = "Trainee username", example = "John.Doe")
        private String username;

        @Schema(description = "Trainee first name", example = "John")
        private String firstName;

        @Schema(description = "Trainee last name", example = "Doe")
        private String lastName;
    }
}

