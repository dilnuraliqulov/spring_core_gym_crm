package com.gymcrm.service;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainee;

import com.gymcrm.service.impl.TraineeServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
        trainee.setUsername("ExistingUser");
        trainee.setPassword("ExistingPass");
    }

    @Test
    void testSaveTrainee_PersistsEntity() {
        // Arrange
        when(traineeDao.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Trainee result = traineeService.save(trainee);

        // Assert
        assertEquals("ExistingUser", result.getUsername());
        assertEquals("ExistingPass", result.getPassword());
        assertEquals("John", result.getFirstName());
        assertEquals("Smith", result.getLastName());

        verify(traineeDao, times(1)).save(trainee);
    }

    @Test
    void testFindTraineeById() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Smith", result.get().getLastName());
        verify(traineeDao, times(1)).findById(1L);
    }

    @Test
    void testFindAllTrainees() {
        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        trainee2.setFirstName("Alice");
        trainee2.setLastName("Brown");

        List<Trainee> trainees = List.of(trainee, trainee2);
        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Alice", result.get(1).getFirstName());

        verify(traineeDao, times(1)).findAll();
    }

    @Test
    void testDeleteTraineeById() {
        traineeService.deleteById(1L);
        verify(traineeDao, times(1)).deleteById(1L);
    }
}
