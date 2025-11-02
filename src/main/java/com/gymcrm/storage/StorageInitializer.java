package com.gymcrm.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymcrm.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class StorageInitializer implements BeanPostProcessor {

    @Value("${data.trainers.path}")
    private String trainersFilePath;

    @Value("${data.trainees.path}")
    private String traineesFilePath;

    @Value("${data.trainings.path}")
    private String trainingsFilePath;

    @Value("${data.trainingtypes.path}")
    private String trainingTypesFilePath;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof InMemoryStorage storage) {
            try {
                loadData(storage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load data into in-memory storage", e);
            }
        }
        return bean;
    }

    private void loadData(InMemoryStorage storage) throws IOException {
        // Read and store Trainers
        List<Trainer> trainers = objectMapper.readValue(new File(trainersFilePath), new TypeReference<>() {});
        trainers.forEach(t -> storage.getTrainerStorage().put(t.getId(), t));

        // Read and store Trainees
        List<Trainee> trainees = objectMapper.readValue(new File(traineesFilePath), new TypeReference<>() {});
        trainees.forEach(t -> storage.getTraineeStorage().put(t.getId(), t));

        // Read and store Trainings
        List<Training> trainings = objectMapper.readValue(new File(trainingsFilePath), new TypeReference<>() {});
        trainings.forEach(t -> storage.getTrainingStorage().put(t.getId(), t));

        // Read and store TrainingTypes
        List<TrainingType> trainingTypes = objectMapper.readValue(new File(trainingTypesFilePath), new TypeReference<>() {});
        trainingTypes.forEach(t -> storage.getTrainingTypeStorage().put(t.getId(), t));

        System.out.println(" In-memory storage initialized successfully!");
    }
}
