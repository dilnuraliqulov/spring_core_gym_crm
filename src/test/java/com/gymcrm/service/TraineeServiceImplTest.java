package com.gymcrm.service;

import com.gymcrm.model.Trainee;
import com.gymcrm.dao.GenericDao;
import com.gymcrm.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
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
    }


    @Test
    void testSaveTrainee_GeneratesUsernameAndPassword() {
        // Arrange
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(traineeDao.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee result = traineeService.save(trainee);

        assertNotNull(result.getUsername(), "Username should be generated");
        assertNotNull(result.getPassword(), "Password should be generated");
        assertEquals(10, result.getPassword().length(), "Password should be 10 characters long");
        assertEquals("John", result.getFirstName());
        assertEquals("Smith", result.getLastName());

        verify(traineeDao, times(1)).findAll();
        verify(traineeDao, times(1)).save(result);
    }

    @Test
    void testSaveTrainee_DuplicateUsername() {
        // Arrange: existing trainee with same name
        Trainee existing = new Trainee();
        existing.setUsername("John.Smith");

        when(traineeDao.findAll()).thenReturn(List.of(existing));
        when(traineeDao.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("John");
        newTrainee.setLastName("Smith");

        Trainee result = traineeService.save(newTrainee);

        assertEquals("John.Smith1", result.getUsername(), "Duplicate username should get numeric suffix");
        assertNotNull(result.getPassword());

        verify(traineeDao, times(1)).findAll();
        verify(traineeDao, times(1)).save(result);
    }

    @Test
    void testSaveTrainee_KeepsExistingUsernamePassword() {
        // Arrange: trainee already has username/password
        trainee.setUsername("ExistingUser");
        trainee.setPassword("ExistingPass");

        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(traineeDao.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee result = traineeService.save(trainee);

        assertEquals("ExistingUser", result.getUsername());
        assertEquals("ExistingPass", result.getPassword());

        verify(traineeDao, times(1)).findAll();
        verify(traineeDao, times(1)).save(result);
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
        verify(traineeDao).deleteById(1L);
    }
}
