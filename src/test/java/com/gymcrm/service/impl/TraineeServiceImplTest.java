package com.gymcrm.service.impl;

import com.gymcrm.model.Trainee;
import com.gymcrm.dao.GenericDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private GenericDao<Trainee> traineeDao;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
    }

    @Test
    void testSaveTrainee() {
        when(traineeDao.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.save(trainee);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(traineeDao, times(1)).save(trainee);
    }

    @Test
    void testFindTraineeById() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Smith", result.get().getLastName());
        verify(traineeDao).findById(1L);
    }

    @Test
    void testDeleteTraineeById() {
        traineeService.deleteById(1L);
        verify(traineeDao).deleteById(1L);
    }
}
