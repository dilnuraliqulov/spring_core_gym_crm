package com.gymcrm.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.model.*;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StorageInitializer {

    @Value("${data.trainers.path}")
    private Resource trainersResource;

    @Value("${data.trainees.path}")
    private Resource traineesResource;

    @Value("${data.trainings.path}")
    private Resource trainingsResource;

    @Value("${data.trainingtypes.path}")
    private Resource trainingTypesResource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TrainerStorage trainerStorage;
    private final TraineeStorage traineeStorage;
    private final TrainingStorage trainingStorage;
    private final TrainingTypeStorage trainingTypeStorage;

    public StorageInitializer(TrainerStorage trainerStorage,
                              TraineeStorage traineeStorage,
                              TrainingStorage trainingStorage,
                              TrainingTypeStorage trainingTypeStorage) {
        this.trainerStorage = trainerStorage;
        this.traineeStorage = traineeStorage;
        this.trainingStorage = trainingStorage;
        this.trainingTypeStorage = trainingTypeStorage;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        loadData();
        log.info("In-memory storages initialized successfully!");
    }

    @SneakyThrows
    private void loadData() {
        // Load Trainers
        List<Trainer> trainers = objectMapper.readValue(
                trainersResource.getInputStream(), new TypeReference<>() {}
        );
        trainers.forEach(t -> trainerStorage.getTrainerStorage().put(t.getId(), t));

        // Load Trainees
        List<Trainee> trainees = objectMapper.readValue(
                traineesResource.getInputStream(), new TypeReference<>() {}
        );
        trainees.forEach(t -> traineeStorage.getTraineeStorage().put(t.getId(), t));

        // Load Trainings
        List<Training> trainings = objectMapper.readValue(
                trainingsResource.getInputStream(), new TypeReference<>() {}
        );
        trainings.forEach(t -> trainingStorage.getTraningStorage().put(t.getId(), t));

        // Load Training Types
        List<TrainingType> trainingTypes = objectMapper.readValue(
                trainingTypesResource.getInputStream(), new TypeReference<>() {}
        );
        trainingTypes.forEach(tt -> trainingTypeStorage.getTrainingTypeStorage().put(tt.getId(), tt));
    }
}
