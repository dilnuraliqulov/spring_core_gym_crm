package com.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer information for unassigned trainers list")
public class TrainerListResponse {

    @Schema(description = "Trainer username", example = "Jane.Smith")
    private String username;

    @Schema(description = "Trainer first name", example = "Jane")
    private String firstName;

    @Schema(description = "Trainer last name", example = "Smith")
    private String lastName;

    @Schema(description = "Trainer specialization (training type)", example = "Fitness")
    private String specialization;
}

