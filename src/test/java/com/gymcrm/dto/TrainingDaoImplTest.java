package com.gymcrm.dto;

import com.gymcrm.dao.impl.TrainingDaoImpl;
import com.gymcrm.model.Training;
import com.gymcrm.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDaoImplTest {

    @Mock
    private TrainingStorage trainingStorage;

    @InjectMocks
    private TrainingDaoImpl trainingDao;

    private Training training;

    @BeforeEach
    void setUp() {
        training = new Training();
        training.setId(1L);
        training.setTraineeId(101L);
        training.setTrainerId(201L);
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.of(2025, 11, 7));
        training.setTrainingDuration(60);
    }

    @Test
    void testSaveNewTraining() {
        when(trainingStorage.containsKey(training.getId())).thenReturn(false);
        when(trainingStorage.put(training.getId(), training)).thenReturn(training);

        Training result = trainingDao.save(training);

        assertNotNull(result);
        assertEquals("Morning Yoga", result.getTrainingName());
        verify(trainingStorage).put(training.getId(), training);
    }

    @Test
    void testSaveExistingTrainingThrowsException() {
        when(trainingStorage.containsKey(training.getId())).thenReturn(true);

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> trainingDao.save(training));

        assertEquals("Updating trainings is not supported", exception.getMessage());
        verify(trainingStorage, never()).put(anyLong(), any());
    }

    @Test
    void testFindById() {
        when(trainingStorage.get(1L)).thenReturn(training);

        Optional<Training> result = trainingDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(101L, result.get().getTraineeId());
        verify(trainingStorage).get(1L);
    }

    @Test
    void testFindAll() {
        Collection<Training> collection = List.of(training);
        when(trainingStorage.values()).thenReturn(collection);

        List<Training> result = trainingDao.findAll();

        assertEquals(1, result.size());
        assertEquals(201L, result.get(0).getTrainerId());
        verify(trainingStorage).values();
    }

    @Test
    void testDeleteByIdThrowsException() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> trainingDao.deleteById(1L));

        assertEquals("Deleting trainings is not supported", exception.getMessage());
    }
}
