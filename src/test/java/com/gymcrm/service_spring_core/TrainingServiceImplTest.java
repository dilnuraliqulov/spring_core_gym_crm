package com.gymcrm.service_spring_core;

import com.gymcrm.service.impl.core.TrainingServiceImpl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private GenericDao<Training> trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training training;

    @BeforeEach
    void setUp() {
        // Initialize TrainingType object
        TrainingType trainingType = new TrainingType(1L, "Yoga");

        // Initialize Training object
        training = new Training();
        training.setId(1L);
        training.setTraineeId(101L);
        training.setTrainerId(201L);
        training.setTrainingName("Morning Yoga");
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDate.of(2025, 11, 6));
        training.setTrainingDuration(60);
    }

    @Test
    void testSaveTraining() {
        when(trainingDao.save(training)).thenReturn(training);

        Training result = trainingService.save(training);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Morning Yoga", result.getTrainingName());
        assertEquals("Yoga", result.getTrainingType().getTrainingTypeName());
        assertEquals(60, result.getTrainingDuration());
        verify(trainingDao, times(1)).save(training);
    }

    @Test
    void testFindTrainingById() {
        when(trainingDao.findById(1L)).thenReturn(Optional.of(training));

        Optional<Training> result = trainingService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Morning Yoga", result.get().getTrainingName());
        assertEquals("Yoga", result.get().getTrainingType().getTrainingTypeName());
        assertEquals(60, result.get().getTrainingDuration());
        verify(trainingDao, times(1)).findById(1L);
    }

    @Test
    void testFindAllTrainings() {
        List<Training> trainings = List.of(training);
        when(trainingDao.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.findAll();

        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
        assertEquals("Yoga", result.get(0).getTrainingType().getTrainingTypeName());
        assertEquals(60, result.get(0).getTrainingDuration());
        verify(trainingDao, times(1)).findAll();
    }

}
