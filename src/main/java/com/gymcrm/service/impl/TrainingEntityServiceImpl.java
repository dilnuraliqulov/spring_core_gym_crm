package com.gymcrm.service.impl;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.exception.TraineeNotFoundException;
import com.gymcrm.exception.TrainerNotFoundException;
import com.gymcrm.exception.TrainingTypeNotFoundException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.repository.TraineeRepository;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.TrainingTypeRepository;
import com.gymcrm.service.TrainingEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("trainingServiceEntity")
@RequiredArgsConstructor
@Transactional
public class TrainingEntityServiceImpl implements TrainingEntityService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public Training addTraining(String traineeUsername, String trainerUsername,
                                 String trainingName, Long trainingTypeId,
                                 Date trainingDate, Integer duration) {
        log.info("Adding training: {} for trainee: {} with trainer: {}", trainingName, traineeUsername, trainerUsername);

        validateTrainingFields(traineeUsername, trainerUsername, trainingName, trainingTypeId, trainingDate, duration);

        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + traineeUsername));

        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + trainerUsername));

        TrainingType trainingType = trainingTypeRepository.findById(trainingTypeId)
                .orElseThrow(() -> new TrainingTypeNotFoundException("Training type not found with id: " + trainingTypeId));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setDurationOfTraining(duration);

        Training savedTraining = trainingRepository.save(training);
        log.info("Training added successfully with id: {}", savedTraining.getId());

        return savedTraining;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> findTrainingById(Long id) {
        log.debug("Finding training by id: {}", id);
        return trainingRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findAllTrainings() {
        log.debug("Finding all trainings");
        return trainingRepository.findAll();
    }

    private void validateTrainingFields(String traineeUsername, String trainerUsername,
                                        String trainingName, Long trainingTypeId,
                                        Date trainingDate, Integer duration) {
        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new ValidationException("Trainee username is required");
        }
        if (trainerUsername == null || trainerUsername.isBlank()) {
            throw new ValidationException("Trainer username is required");
        }
        if (trainingName == null || trainingName.isBlank()) {
            throw new ValidationException("Training name is required");
        }
        if (trainingTypeId == null) {
            throw new ValidationException("Training type is required");
        }
        if (trainingDate == null) {
            throw new ValidationException("Training date is required");
        }
        if (duration == null || duration <= 0) {
            throw new ValidationException("Training duration must be a positive number");
        }
    }
}

