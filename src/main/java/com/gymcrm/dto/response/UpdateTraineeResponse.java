package com.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Updated trainee profile response")
public class UpdateTraineeResponse {

    @Schema(description = "Username", example = "John.Doe")
    private String username;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Date of birth", example = "1990-01-15")
    private Date dateOfBirth;

    @Schema(description = "Address", example = "123 Main St, City")
    private String address;

    @Schema(description = "Whether the trainee is active", example = "true")
    private Boolean isActive;

    @Schema(description = "List of assigned trainers")
    private List<TraineeProfileResponse.TrainerInfo> trainers;
}

