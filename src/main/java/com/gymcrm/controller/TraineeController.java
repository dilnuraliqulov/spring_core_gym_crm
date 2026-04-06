package com.gymcrm.controller;

import com.gymcrm.dto.request.ActivationRequest;
import com.gymcrm.dto.request.TraineeRegistrationRequest;
import com.gymcrm.dto.request.UpdateTraineeRequest;
import com.gymcrm.dto.request.UpdateTraineeTrainersRequest;
import com.gymcrm.dto.response.*;
import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.User;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.exception.InvalidOperationException;
import com.gymcrm.exception.TraineeNotFoundException;
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
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainee", description = "Trainee management APIs")
public class TraineeController {

    private final TraineeEntityService traineeService;
    private final TrainerEntityService trainerService;
    private final UserService userService;

    @Operation(summary = "Register a new trainee", description = "Creates a new trainee profile with auto-generated username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee registered successfully",
                    content = @Content(schema = @Schema(implementation = RegistrationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<RegistrationResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest request) {
        log.info("Registering new trainee: {} {}", request.getFirstName(), request.getLastName());

        // Check if user already exists as trainer
        if (trainerService.findByUsername(request.getFirstName() + "." + request.getLastName()).isPresent()) {
            throw new InvalidOperationException("User is already registered as a trainer");
        }

        Trainee trainee = traineeService.createProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress()
        );

        RegistrationResponse response = new RegistrationResponse(
                trainee.getUser().getUsername(),
                new String(trainee.getUser().getPassword())
        );

        log.info("Trainee registered successfully with username: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get trainee profile", description = "Retrieves trainee profile by username (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TraineeProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(username, password);

        Trainee trainee = traineeService.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + username));

        TraineeProfileResponse response = mapToTraineeProfile(trainee);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update trainee profile", description = "Updates trainee profile (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UpdateTraineeResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<UpdateTraineeResponse> updateTraineeProfile(
            @Valid @RequestBody UpdateTraineeRequest request,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(request.getUsername(), password);

        Trainee updatedTrainee = traineeService.updateProfile(
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress(),
                request.getIsActive()
        );

        UpdateTraineeResponse response = mapToUpdateTraineeResponse(updatedTrainee);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete trainee profile", description = "Hard deletes trainee profile and related trainings (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(username, password);

        traineeService.deleteByUsername(username);
        log.info("Trainee deleted: {}", username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get unassigned active trainers", description = "Gets list of trainers not assigned to this trainee (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<List<TrainerListResponse>> getUnassignedTrainers(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(username, password);

        List<Trainer> trainers = traineeService.getUnassignedTrainers(username);
        List<TrainerListResponse> response = trainers.stream()
                .map(this::mapToTrainerListResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update trainee's trainer list", description = "Updates the list of trainers assigned to trainee (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers list updated successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/trainers")
    public ResponseEntity<List<TrainerListResponse>> updateTraineeTrainers(
            @Valid @RequestBody UpdateTraineeTrainersRequest request,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        authenticate(request.getTraineeUsername(), password);

        Trainee updatedTrainee = traineeService.updateTrainersList(
                request.getTraineeUsername(),
                request.getTrainerUsernames()
        );

        List<TrainerListResponse> response = updatedTrainee.getTrainers().stream()
                .map(this::mapToTrainerListResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get trainee's trainings", description = "Gets filtered list of trainee's trainings (requires authentication)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings list retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingResponse>> getTraineeTrainings(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable String username,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password,
            @Parameter(description = "Filter by period start date")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodFrom,
            @Parameter(description = "Filter by period end date")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodTo,
            @Parameter(description = "Filter by trainer name")
            @RequestParam(required = false) String trainerName,
            @Parameter(description = "Filter by training type")
            @RequestParam(required = false) String trainingType) {

        authenticate(username, password);

        List<Training> trainings = traineeService.getTrainings(username, periodFrom, periodTo, trainerName, trainingType);
        List<TrainingResponse> response = trainings.stream()
                .map(this::mapToTrainingResponseForTrainee)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate/Deactivate trainee", description = "Changes trainee active status (requires authentication). This is not an idempotent action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid operation (already in requested state)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/activate")
    public ResponseEntity<Void> activateDeactivateTrainee(
            @Valid @RequestBody ActivationRequest request,
            @Parameter(description = "Password for authentication", required = true)
            @RequestHeader("X-Password") String password) {

        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + request.getUsername()));

        if (!new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .matches(password, new String(user.getPassword()))) {
            throw new AuthenticationException("Invalid username or password");
        }
        Trainee trainee = traineeService.findByUsername(request.getUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + request.getUsername()));

        // Not idempotent - check current state
        boolean currentState = trainee.getUser().isActive();
        if (currentState == request.getIsActive()) {
            throw new InvalidOperationException(
                    "Trainee is already " + (currentState ? "active" : "inactive"));
        }

        traineeService.updateProfile(
                request.getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                request.getIsActive()
        );

        log.info("Trainee {} {}", request.getUsername(), request.getIsActive() ? "activated" : "deactivated");
        return ResponseEntity.ok().build();
    }

    private void authenticate(String username, String password) {
        if (!userService.authenticate(username, password.toCharArray())) {
            throw new AuthenticationException("Invalid username or password");
        }
    }

    private TraineeProfileResponse mapToTraineeProfile(Trainee trainee) {
        List<TraineeProfileResponse.TrainerInfo> trainers = trainee.getTrainers().stream()
                .map(trainer -> new TraineeProfileResponse.TrainerInfo(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization() != null
                                ? trainer.getSpecialization().getTrainingTypeName() : null
                ))
                .collect(Collectors.toList());

        return new TraineeProfileResponse(
                trainee.getUser().getUsername(),   // ADD username as first argument
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().isActive(),
                trainers
        );
    }

    private UpdateTraineeResponse mapToUpdateTraineeResponse(Trainee trainee) {
        List<TraineeProfileResponse.TrainerInfo> trainers = trainee.getTrainers().stream()
                .map(trainer -> new TraineeProfileResponse.TrainerInfo(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization() != null ? trainer.getSpecialization().getTrainingTypeName() : null
                ))
                .collect(Collectors.toList());

        return new UpdateTraineeResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().isActive(),
                trainers
        );
    }

    private TrainerListResponse mapToTrainerListResponse(Trainer trainer) {
        return new TrainerListResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization() != null ? trainer.getSpecialization().getTrainingTypeName() : null
        );
    }

    private TrainingResponse mapToTrainingResponseForTrainee(Training training) {
        TrainingResponse response = new TrainingResponse();
        response.setTrainingName(training.getTrainingName());
        response.setTrainingDate(training.getTrainingDate());
        response.setTrainingType(training.getTrainingType() != null ? training.getTrainingType().getTrainingTypeName() : null);
        response.setDuration(training.getDurationOfTraining());
        response.setTrainerName(training.getTrainer().getUser().getFirstName() + " " +
                training.getTrainer().getUser().getLastName());
        return response;
    }
}

