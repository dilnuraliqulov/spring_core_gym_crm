package com.gymcrm.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
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
        loadList(trainersResource, trainerStorage);
        loadList(traineesResource, traineeStorage);
        loadList(trainingsResource, trainingStorage);
        loadList(trainingTypesResource, trainingTypeStorage);
        log.info("In-memory storages initialized successfully!");
    }

    @SneakyThrows
    private <T> void loadList(Resource resource, TypedStorage<T> storage) {
        List<T> list = objectMapper.readValue(
                resource.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, storage.getType())
        );
        list.forEach(item -> storage.put(getId(item), item));
    }

    @SuppressWarnings("unchecked")
    private <T> Long getId(T item) {
        try {
            Method getIdMethod = item.getClass().getMethod("getId");
            return (Long) getIdMethod.invoke(item);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get id from item: " + item, e);
        }
    }
}
