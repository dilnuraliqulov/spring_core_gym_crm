package com.gymcrm.service.impl;


import com.gymcrm.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
    }

    @Test
    void testCreateTrainee() {
        when(traineeDao.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.createTrainee(trainee);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(traineeDao, times(1)).save(trainee);
    }

    @Test
    void testGetTraineeById() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeService.getTraineeById(1L);

        assertTrue(result.isPresent());
        assertEquals("Smith", result.get().getLastName());
        verify(traineeDao).findById(1L);
    }

    @Test
    void testDeleteTrainee() {
        traineeService.deleteTrainee(1L);
        verify(traineeDao).delete(1L);
    }
}
