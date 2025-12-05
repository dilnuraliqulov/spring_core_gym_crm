package com.gymcrm.facade;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import com.gymcrm.util.UsernamePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    //Trainee operations
    public Trainee createTrainee(Trainee trainee) {
        log.info("Creating trainee: {}", trainee);

        // Collect existing usernames
        Set<String> existingUsernames = traineeService.findAll().stream()
                .map(Trainee::getUsername)
                .collect(Collectors.toSet());

        // Generate username if missing
        if (trainee.getUsername() == null || trainee.getUsername().isBlank()) {
            String username = UsernamePasswordGenerator.generateUsername(
                    trainee.getFirstName(),
                    trainee.getLastName(),
                    existingUsernames
            );
            trainee.setUsername(username);
            log.debug("Generated username for trainee: {}", username);
        }

        // Generate password if missing
        if (trainee.getPassword() == null || trainee.getPassword().isBlank()) {
            char[] password = UsernamePasswordGenerator.generatePassword(10);
            trainee.setPassword(new String(password));
            log.debug("Generated password for trainee {}: {}", trainee.getUsername(), trainee.getPassword());

        }
        return traineeService.save(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        log.info("Updating trainee: {}", trainee);
        return traineeService.save(trainee);
    }

    public void deleteTrainee(Long id) {
        log.info("Deleting trainee with id: {}", id);
        traineeService.deleteById(id);
    }

    public Optional<Trainee> getTraineeById(Long id) {
        log.info("Fetching trainee with id: {}", id);
        return traineeService.findById(id);
    }

    public List<Trainee> getAllTrainees() {
        log.info("Fetching all trainees");
        return traineeService.findAll();
    }

    //Trainer operations
    public Trainer createTrainer(Trainer trainer) {
        log.info("Creating trainer: {}", trainer);

        Set<String> existingUsernames = trainerService.findAll().stream()
                .map(Trainer::getUsername)
                .collect(Collectors.toSet());

        if (trainer.getUsername() == null || trainer.getUsername().isBlank()) {
            String username = UsernamePasswordGenerator.generateUsername(
                    trainer.getFirstName(),
                    trainer.getLastName(),
                    existingUsernames
            );
            trainer.setUsername(username);
            log.debug("Generated username for trainer: {}", username);
        }

        if (trainer.getPassword() == null || trainer.getPassword().isBlank()) {
            char[] password = UsernamePasswordGenerator.generatePassword(10);
            trainer.setPassword(new String(password));
            log.debug("Generated password for trainer {}: {}", trainer.getUsername(), trainer.getPassword());

        }

        return trainerService.save(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        log.info("Updating trainer: {}", trainer);
        return trainerService.save(trainer);
    }

    public Optional<Trainer> getTrainerById(Long id) {
        log.info("Fetching trainer with id: {}", id);
        return trainerService.findById(id);
    }

    public List<Trainer> getAllTrainers() {
        log.info("Fetching all trainers");
        return trainerService.findAll();
    }

    //Training operations

    public Training createTraining(Training training) {
        log.info("Creating training: {}", training);
        return trainingService.save(training); // save() is create-only
    }

    public Optional<Training> getTrainingById(Long id) {
        log.info("Fetching training with id: {}", id);
        return trainingService.findById(id);
    }

    public List<Training> getAllTrainings() {
        log.info("Fetching all trainings");
        return trainingService.findAll();
    }

}
