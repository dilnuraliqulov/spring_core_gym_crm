package com.gymcrm.facade;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Entity-based facade for gym operations.
 * All operations except profile creation require authentication.
 * Uses Interface Segregation Principle - injects only the contracts needed.
 */
@Slf4j
@Component
public class GymEntityFacade {

    private final TraineeEntityService traineeEntityService;
    private final TrainerEntityService trainerEntityService;
    private final TrainingEntityService trainingEntityService;
    private final TrainingTypeService trainingTypeService;

    // Separate contracts for authentication and activation (ISP)
    private final AuthenticationService traineeAuthService;
    private final AuthenticationService trainerAuthService;
    private final ActivationService traineeActivationService;
    private final ActivationService trainerActivationService;

    public GymEntityFacade(
            TraineeEntityService traineeEntityService,
            TrainerEntityService trainerEntityService,
            TrainingEntityService trainingEntityService,
            TrainingTypeService trainingTypeService,
            @Qualifier("traineeServiceEntity") AuthenticationService traineeAuthService,
            @Qualifier("trainerServiceEntity") AuthenticationService trainerAuthService,
            @Qualifier("traineeServiceEntity") ActivationService traineeActivationService,
            @Qualifier("trainerServiceEntity") ActivationService trainerActivationService) {
        this.traineeEntityService = traineeEntityService;
        this.trainerEntityService = trainerEntityService;
        this.trainingEntityService = trainingEntityService;
        this.trainingTypeService = trainingTypeService;
        this.traineeAuthService = traineeAuthService;
        this.trainerAuthService = trainerAuthService;
        this.traineeActivationService = traineeActivationService;
        this.trainerActivationService = trainerActivationService;
    }

    public Trainee createTraineeProfile(String firstName, String lastName, Date dateOfBirth, String address) {
        log.info("Creating trainee profile for: {} {}", firstName, lastName);
        return traineeEntityService.createProfile(firstName, lastName, dateOfBirth, address);
    }

    public boolean authenticateTrainee(String username, char[] password) {
        log.info("Authenticating trainee: {}", username);
        return traineeAuthService.authenticate(username, password);
    }

    public Optional<Trainee> getTraineeByUsername(String username, char[] password) {
        authenticateTrainee(username, password);
        log.info("Fetching trainee profile: {}", username);
        return traineeEntityService.findByUsername(username);
    }

    public void changeTraineePassword(String username, char[] currentPassword, char[] newPassword) {
        log.info("Changing password for trainee: {}", username);
        traineeAuthService.changePassword(username, currentPassword, newPassword);
    }

    public Trainee updateTraineeProfile(String username, char[] password, String firstName,
                                         String lastName, Date dateOfBirth, String address, boolean isActive) {
        authenticateTrainee(username, password);
        log.info("Updating trainee profile: {}", username);
        return traineeEntityService.updateProfile(username, firstName, lastName, dateOfBirth, address, isActive);
    }

    public void activateTrainee(String username, char[] password) {
        authenticateTrainee(username, password);
        log.info("Activating trainee: {}", username);
        traineeActivationService.activate(username);
    }

    public void deactivateTrainee(String username, char[] password) {
        authenticateTrainee(username, password);
        log.info("Deactivating trainee: {}", username);
        traineeActivationService.deactivate(username);
    }

    public void deleteTraineeByUsername(String username, char[] password) {
        authenticateTrainee(username, password);
        log.info("Deleting trainee: {}", username);
        traineeEntityService.deleteByUsername(username);
    }

    public List<Training> getTraineeTrainings(String username, char[] password,
                                               Date fromDate, Date toDate,
                                               String trainerName, String trainingTypeName) {
        authenticateTrainee(username, password);
        log.info("Getting trainings for trainee: {}", username);
        return traineeEntityService.getTrainings(username, fromDate, toDate, trainerName, trainingTypeName);
    }


    public List<Trainer> getUnassignedTrainers(String traineeUsername, char[] password) {
        authenticateTrainee(traineeUsername, password);
        log.info("Getting unassigned trainers for trainee: {}", traineeUsername);
        return traineeEntityService.getUnassignedTrainers(traineeUsername);
    }


    public Trainee updateTraineeTrainersList(String username, char[] password, List<String> trainerUsernames) {
        authenticateTrainee(username, password);
        log.info("Updating trainers list for trainee: {}", username);
        return traineeEntityService.updateTrainersList(username, trainerUsernames);
    }


    public Trainer createTrainerProfile(String firstName, String lastName, Long specializationId) {
        log.info("Creating trainer profile for: {} {}", firstName, lastName);
        return trainerEntityService.createProfile(firstName, lastName, specializationId);
    }


    public boolean authenticateTrainer(String username, char[] password) {
        log.info("Authenticating trainer: {}", username);
        return trainerAuthService.authenticate(username, password);
    }


    public Optional<Trainer> getTrainerByUsername(String username, char[] password) {
        authenticateTrainer(username, password);
        log.info("Fetching trainer profile: {}", username);
        return trainerEntityService.findByUsername(username);
    }


    public void changeTrainerPassword(String username, char[] currentPassword, char[] newPassword) {
        log.info("Changing password for trainer: {}", username);
        trainerAuthService.changePassword(username, currentPassword, newPassword);
    }


    public Trainer updateTrainerProfile(String username, char[] password, String firstName,
                                         String lastName, Long specializationId, boolean isActive) {
        authenticateTrainer(username, password);
        log.info("Updating trainer profile: {}", username);
        return trainerEntityService.updateProfile(username, firstName, lastName, specializationId, isActive);
    }


    public void activateTrainer(String username, char[] password) {
        authenticateTrainer(username, password);
        log.info("Activating trainer: {}", username);
        trainerActivationService.activate(username);
    }


    public void deactivateTrainer(String username, char[] password) {
        authenticateTrainer(username, password);
        log.info("Deactivating trainer: {}", username);
        trainerActivationService.deactivate(username);
    }


    public List<Training> getTrainerTrainings(String username, char[] password,
                                               Date fromDate, Date toDate, String traineeName) {
        authenticateTrainer(username, password);
        log.info("Getting trainings for trainer: {}", username);
        return trainerEntityService.getTrainings(username, fromDate, toDate, traineeName);
    }


    public Training addTraining(String traineeUsername, char[] traineePassword,
                                String trainerUsername, String trainingName,
                                Long trainingTypeId, Date trainingDate, Integer duration) {
        authenticateTrainee(traineeUsername, traineePassword);
        log.info("Adding training for trainee: {} with trainer: {}", traineeUsername, trainerUsername);
        return trainingEntityService.addTraining(traineeUsername, trainerUsername, trainingName,
                                                  trainingTypeId, trainingDate, duration);
    }


    public List<TrainingType> getAllTrainingTypes() {
        log.info("Fetching all training types");
        return trainingTypeService.findAll();
    }


    public Optional<TrainingType> getTrainingTypeById(Long id) {
        log.info("Fetching training type with id: {}", id);
        return trainingTypeService.findById(id);
    }
}
