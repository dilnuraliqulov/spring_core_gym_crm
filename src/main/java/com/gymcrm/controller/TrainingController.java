package com.gymcrm.controller;

import com.gymcrm.dto.request.AddTrainingRequest;
import com.gymcrm.dto.response.ErrorResponse;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.service.TrainingEntityService;
import com.gymcrm.service.TrainingTypeService;
import com.gymcrm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Training management APIs")
public class TrainingController {

    private final TrainingEntityService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;

    @Operation(summary = "Add training", description = "Creates a new training session (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee, trainer or training type not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Void> addTraining(
            @Valid @RequestBody AddTrainingRequest request,
            @Parameter(description = "Username for authentication", required = true)
            @RequestHeader("X-Username") String authUsername,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(authUsername, password);

        log.info("Adding training: {} for trainee: {} with trainer: {}",
                request.getTrainingName(), request.getTraineeUsername(), request.getTrainerUsername());

        // Get the training type from the trainer's specialization
        trainingService.addTraining(
                request.getTraineeUsername(),
                request.getTrainerUsername(),
                request.getTrainingName(),
                null,
                request.getTrainingDate(),
                request.getDuration()
        );

        log.info("Training added successfully");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete training", description = "Cancels/deletes a training session (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Training not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{trainingId}")
    public ResponseEntity<Void> deleteTraining(
            @Parameter(description = "Training ID to delete", required = true)
            @PathVariable Long trainingId,
            @Parameter(description = "Username for authentication", required = true)
            @RequestHeader("X-Username") String authUsername,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(authUsername, password);

        log.info("Deleting training with id: {}", trainingId);
        trainingService.deleteTraining(trainingId);
        log.info("Training deleted successfully");

        return ResponseEntity.ok().build();
    }

    private void authenticate(String username, String password) {
        if (!userService.authenticate(username, password.toCharArray())) {
            throw new AuthenticationException("Invalid username or password");
        }
    }
}

