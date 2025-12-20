package com.gymcrm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for adding a training")
public class AddTrainingRequest {

    @NotBlank(message = "Trainee username is required")
    @Schema(description = "Username of the trainee", required = true, example = "John.Doe")
    private String traineeUsername;

    @NotBlank(message = "Trainer username is required")
    @Schema(description = "Username of the trainer", required = true, example = "Jane.Smith")
    private String trainerUsername;

    @NotBlank(message = "Training name is required")
    @Schema(description = "Name of the training", required = true, example = "Morning Yoga Session")
    private String trainingName;

    @NotNull(message = "Training date is required")
    @Schema(description = "Date of the training", required = true, example = "2024-12-20")
    private Date trainingDate;

    @NotNull(message = "Training duration is required")
    @Positive(message = "Duration must be positive")
    @Schema(description = "Duration of the training in minutes", required = true, example = "60")
    private Integer duration;
}

