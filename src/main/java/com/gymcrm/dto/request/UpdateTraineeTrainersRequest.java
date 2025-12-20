package com.gymcrm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for updating trainee's trainer list")
public class UpdateTraineeTrainersRequest {

    @NotBlank(message = "Trainee username is required")
    @Schema(description = "Username of the trainee", required = true, example = "John.Doe")
    private String traineeUsername;

    @NotEmpty(message = "Trainers list cannot be empty")
    @Schema(description = "List of trainer usernames", required = true)
    private List<String> trainerUsernames;
}

