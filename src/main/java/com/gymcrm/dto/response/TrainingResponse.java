package com.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Training information response")
public class TrainingResponse {

    @Schema(description = "Training name", example = "Morning Yoga Session")
    private String trainingName;

    @Schema(description = "Training date", example = "2024-12-20")
    private Date trainingDate;

    @Schema(description = "Training type", example = "Yoga")
    private String trainingType;

    @Schema(description = "Training duration in minutes", example = "60")
    private Integer duration;

    @Schema(description = "Trainer name (for trainee's training list)", example = "Jane Smith")
    private String trainerName;

    @Schema(description = "Trainee name (for trainer's training list)", example = "John Doe")
    private String traineeName;
}

