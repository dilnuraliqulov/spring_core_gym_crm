package com.gymcrm.facade;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    //Trainee operations

    public Trainee createTrainee(Trainee trainee) {
        log.debug("Facade:Creating trainee: {}", trainee);
        return traineeService.save(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        log.debug("Facade:Updating trainee: {}", trainee);
        return traineeService.save(trainee);
    }

    public void deleteTrainee(Long id) {
        log.debug("Facade:Deleting trainee with id: {}", id);
        traineeService.deleteById(id);
    }

    public Optional<Trainee> getTraineeById(Long id) {
        log.debug("Facade:Fetching trainee with id: {}", id);
        return traineeService.findById(id);
    }

    public List<Trainee> getAllTrainees() {
        log.debug("Facade:Fetching all trainees");
        return traineeService.findAll();
    }

    //Trainer operations

    public Trainer createTrainer(Trainer trainer) {
        log.debug("Facade:Creating trainer: {}", trainer);
        return trainerService.save(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        log.debug("Facade:Updating trainer: {}", trainer);
        return trainerService.save(trainer);
    }

    public Optional<Trainer> getTrainerById(Long id) {
        log.debug("Facade:Fetching trainer with id: {}", id);
        return trainerService.findById(id);
    }

    public List<Trainer> getAllTrainers() {
        log.debug("Facade:Fetching all trainers");
        return trainerService.findAll();
    }

    //Training operations

    public Training createTraining(Training training) {
        log.debug("Facade:Creating training: {}", training);
        return trainingService.save(training); // save() is create-only
    }

    public Optional<Training> getTrainingById(Long id) {
        log.debug("Facade:Fetching training with id: {}", id);
        return trainingService.findById(id);
    }

    public List<Training> getAllTrainings() {
        log.debug("Facade:Fetching all trainings");
        return trainingService.findAll();
    }

}
