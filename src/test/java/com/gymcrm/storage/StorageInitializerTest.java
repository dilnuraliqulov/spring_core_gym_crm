package com.gymcrm.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageInitializerTest {

    @Mock
    private TrainerStorage trainerStorage;

    @Mock
    private TraineeStorage traineeStorage;

    @Mock
    private TrainingStorage trainingStorage;

    @Mock
    private TrainingTypeStorage trainingTypeStorage;

    @Mock
    private Resource trainersResource;

    @Mock
    private Resource traineesResource;

    @Mock
    private Resource trainingsResource;

    @Mock
    private Resource trainingTypesResource;

    @InjectMocks
    private StorageInitializer storageInitializer;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testInitLoadsAllStorages() throws Exception {
        // Prepare JSON data for each resource
        String trainerJson = "[{\"id\":1,\"firstName\":\"Alice\"}]";
        String traineeJson = "[{\"id\":1,\"firstName\":\"John\"}]";
        String trainingJson = "[{\"id\":1,\"name\":\"Yoga\"}]";
        String trainingTypeJson = "[{\"id\":1,\"name\":\"Cardio\"}]";

        when(trainersResource.getInputStream())
                .thenReturn(new ByteArrayInputStream(trainerJson.getBytes(StandardCharsets.UTF_8)));
        when(traineesResource.getInputStream())
                .thenReturn(new ByteArrayInputStream(traineeJson.getBytes(StandardCharsets.UTF_8)));
        when(trainingsResource.getInputStream())
                .thenReturn(new ByteArrayInputStream(trainingJson.getBytes(StandardCharsets.UTF_8)));
        when(trainingTypesResource.getInputStream())
                .thenReturn(new ByteArrayInputStream(trainingTypeJson.getBytes(StandardCharsets.UTF_8)));

        // Mock getType() method of storages
        when(trainerStorage.getType()).thenReturn(Trainer.class);
        when(traineeStorage.getType()).thenReturn(Trainee.class);
        when(trainingStorage.getType()).thenReturn(Training.class);
        when(trainingTypeStorage.getType()).thenReturn(TrainingType.class);

        // Call init()
        storageInitializer.init();

        // Verify that data was put into storages
        verify(trainerStorage).put(1L, any(Trainer.class));
        verify(traineeStorage).put(1L, any(Trainee.class));
        verify(trainingStorage).put(1L, any(Training.class));
        verify(trainingTypeStorage).put(1L, any(TrainingType.class));
    }
}
