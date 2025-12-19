package com.gymcrm.controller;

import com.gymcrm.dto.request.ActivationRequest;
import com.gymcrm.dto.request.TrainerRegistrationRequest;
import com.gymcrm.dto.request.UpdateTrainerRequest;
import com.gymcrm.dto.response.*;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.exception.InvalidOperationException;
import com.gymcrm.exception.TrainerNotFoundException;
import com.gymcrm.service.TraineeEntityService;
import com.gymcrm.service.TrainerEntityService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainer", description = "Trainer management APIs")
public class TrainerController {

    private final TrainerEntityService trainerService;
    private final TraineeEntityService traineeService;
    private final UserService userService;

    @Operation(summary = "Register a new trainer", description = "Creates a new trainer profile with auto-generated username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer registered successfully",
                    content = @Content(schema = @Schema(implementation = RegistrationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<RegistrationResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest request) {
        log.info("Registering new trainer: {} {}", request.getFirstName(), request.getLastName());

        // Check if user already exists as trainee
        if (traineeService.findByUsername(request.getFirstName() + "." + request.getLastName()).isPresent()) {
            throw new InvalidOperationException("User is already registered as a trainee");
        }

        Trainer trainer = trainerService.createProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecializationId()
        );

        RegistrationResponse response = new RegistrationResponse(
                trainer.getUser().getUsername(),
                new String(trainer.getUser().getPassword())
        );

        log.info("Trainer registered successfully with username: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get trainer profile", description = "Retrieves trainer profile by username (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TrainerProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(
            @Parameter(description = "Trainer username", required = true)
            @PathVariable String username,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(username, password);

        Trainer trainer = trainerService.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + username));

        TrainerProfileResponse response = mapToTrainerProfile(trainer);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update trainer profile", description = "Updates trainer profile. Note: Specialization cannot be changed (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UpdateTrainerResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<UpdateTrainerResponse> updateTrainerProfile(
            @Valid @RequestBody UpdateTrainerRequest request,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(request.getUsername(), password);

        // Get current trainer to preserve specialization (read-only)
        Trainer currentTrainer = trainerService.findByUsername(request.getUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + request.getUsername()));

        Long specializationId = currentTrainer.getSpecialization() != null
                ? currentTrainer.getSpecialization().getId()
                : null;

        Trainer updatedTrainer = trainerService.updateProfile(
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                specializationId, // Preserve original specialization (read-only)
                request.getIsActive()
        );

        UpdateTrainerResponse response = mapToUpdateTrainerResponse(updatedTrainer);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get trainer's trainings", description = "Gets filtered list of trainer's trainings (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings list retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainings(
            @Parameter(description = "Trainer username", required = true)
            @PathVariable String username,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password,
            @Parameter(description = "Filter by period start date")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodFrom,
            @Parameter(description = "Filter by period end date")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodTo,
            @Parameter(description = "Filter by trainee name")
            @RequestParam(required = false) String traineeName) {

        authenticate(username, password);

        List<Training> trainings = trainerService.getTrainings(username, periodFrom, periodTo, traineeName);
        List<TrainingResponse> response = trainings.stream()
                .map(this::mapToTrainingResponseForTrainer)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate/Deactivate trainer", description = "Changes trainer active status (requires authentication). This is not an idempotent action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid operation (already in requested state)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/activate")
    public ResponseEntity<Void> activateDeactivateTrainer(
            @Valid @RequestBody ActivationRequest request,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(request.getUsername(), password);

        Trainer trainer = trainerService.findByUsername(request.getUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + request.getUsername()));

        // Not idempotent - check current state
        boolean currentState = trainer.getUser().isActive();
        if (currentState == request.getIsActive()) {
            throw new InvalidOperationException(
                    "Trainer is already " + (currentState ? "active" : "inactive"));
        }

        Long specializationId = trainer.getSpecialization() != null
                ? trainer.getSpecialization().getId()
                : null;

        trainerService.updateProfile(
                request.getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                specializationId,
                request.getIsActive()
        );

        log.info("Trainer {} {}", request.getUsername(), request.getIsActive() ? "activated" : "deactivated");
        return ResponseEntity.ok().build();
    }

    private void authenticate(String username, String password) {
        if (!userService.authenticate(username, password.toCharArray())) {
            throw new AuthenticationException("Invalid username or password");
        }
    }

    private TrainerProfileResponse mapToTrainerProfile(Trainer trainer) {
        List<TrainerProfileResponse.TraineeInfo> trainees = trainer.getTrainees().stream()
                .map(trainee -> new TrainerProfileResponse.TraineeInfo(
                        trainee.getUser().getUsername(),
                        trainee.getUser().getFirstName(),
                        trainee.getUser().getLastName()
                ))
                .collect(Collectors.toList());

        return new TrainerProfileResponse(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization() != null ? trainer.getSpecialization().getTrainingTypeName() : null,
                trainer.getUser().isActive(),
                trainees
        );
    }

    private UpdateTrainerResponse mapToUpdateTrainerResponse(Trainer trainer) {
        List<TrainerProfileResponse.TraineeInfo> trainees = trainer.getTrainees().stream()
                .map(trainee -> new TrainerProfileResponse.TraineeInfo(
                        trainee.getUser().getUsername(),
                        trainee.getUser().getFirstName(),
                        trainee.getUser().getLastName()
                ))
                .collect(Collectors.toList());

        return new UpdateTrainerResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization() != null ? trainer.getSpecialization().getTrainingTypeName() : null,
                trainer.getUser().isActive(),
                trainees
        );
    }

    private TrainingResponse mapToTrainingResponseForTrainer(Training training) {
        TrainingResponse response = new TrainingResponse();
        response.setTrainingName(training.getTrainingName());
        response.setTrainingDate(training.getTrainingDate());
        response.setTrainingType(training.getTrainingType() != null ? training.getTrainingType().getTrainingTypeName() : null);
        response.setDuration(training.getDurationOfTraining());
        response.setTraineeName(training.getTrainee().getUser().getFirstName() + " " +
                training.getTrainee().getUser().getLastName());
        return response;
    }
}

