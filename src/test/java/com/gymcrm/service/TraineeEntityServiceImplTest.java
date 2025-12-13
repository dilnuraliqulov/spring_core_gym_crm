package com.gymcrm.service;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.User;
import com.gymcrm.exception.InvalidOperationException;
import com.gymcrm.exception.TraineeNotFoundException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.repository.TraineeRepository;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.UserRepository;
import com.gymcrm.service.impl.TraineeEntityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeEntityServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TraineeEntityServiceImpl traineeEntityService;

    private User testUser;
    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("John.Doe");
        testUser.setPassword("hashedPassword".toCharArray());
        testUser.setActive(true);

        testTrainee = new Trainee();
        testTrainee.setId(1L);
        testTrainee.setUser(testUser);
        testTrainee.setDateOfBirth(new Date());
        testTrainee.setAddress("123 Main St");
    }


    @Test
    void createProfile_success() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userService.hashPassword(any(char[].class))).thenReturn("encodedPassword".toCharArray());
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(i -> {
            Trainee t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        Trainee result = traineeEntityService.createProfile("John", "Doe", new Date(), "123 Main St");

        assertNotNull(result);
        assertEquals("John.Doe", result.getUser().getUsername());
        assertTrue(result.getUser().isActive());
        verify(traineeRepository).save(any(Trainee.class));
        verify(userService).hashPassword(any(char[].class));
    }

    @Test
    void createProfile_withExistingUsername_generatesUnique() {
        User existingUser = new User();
        existingUser.setUsername("John.Doe");
        when(userRepository.findAll()).thenReturn(List.of(existingUser));
        when(userService.hashPassword(any(char[].class))).thenReturn("encodedPassword".toCharArray());
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(i -> {
            Trainee t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        Trainee result = traineeEntityService.createProfile("John", "Doe", new Date(), "123 Main St");

        assertNotNull(result);
        assertEquals("John.Doe1", result.getUser().getUsername());
    }

    @Test
    void createProfile_withBlankFirstName_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                traineeEntityService.createProfile("", "Doe", new Date(), "123 Main St"));
    }

    @Test
    void createProfile_withNullLastName_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                traineeEntityService.createProfile("John", null, new Date(), "123 Main St"));
    }

    @Test
    void authenticate_success() {
        when(traineeRepository.existsByUserUsername("John.Doe")).thenReturn(true);
        when(userService.authenticate("John.Doe", "password".toCharArray())).thenReturn(true);

        boolean result = traineeEntityService.authenticate("John.Doe", "password".toCharArray());

        assertTrue(result);
        verify(userService).authenticate("John.Doe", "password".toCharArray());
    }

    @Test
    void authenticate_traineeNotFound_throwsException() {
        when(traineeRepository.existsByUserUsername("unknown")).thenReturn(false);

        assertThrows(TraineeNotFoundException.class, () ->
                traineeEntityService.authenticate("unknown", "password".toCharArray()));
    }

    @Test
    void authenticate_delegatesToUserService() {
        when(traineeRepository.existsByUserUsername("John.Doe")).thenReturn(true);
        when(userService.authenticate("John.Doe", "wrongPassword".toCharArray()))
                .thenThrow(new BadCredentialsException("Invalid password"));

        assertThrows(BadCredentialsException.class, () ->
                traineeEntityService.authenticate("John.Doe", "wrongPassword".toCharArray()));
    }


    @Test
    void changePassword_success() {
        when(traineeRepository.existsByUserUsername("John.Doe")).thenReturn(true);

        traineeEntityService.changePassword("John.Doe", "currentPassword".toCharArray(), "newPassword".toCharArray());

        verify(userService).changePassword("John.Doe", "currentPassword".toCharArray(), "newPassword".toCharArray());
    }

    @Test
    void changePassword_traineeNotFound_throwsException() {
        when(traineeRepository.existsByUserUsername("unknown")).thenReturn(false);

        assertThrows(TraineeNotFoundException.class, () ->
                traineeEntityService.changePassword("unknown", "current".toCharArray(), "new".toCharArray()));
    }


    @Test
    void activate_success() {
        testUser.setActive(false);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        traineeEntityService.activate("John.Doe");

        assertTrue(testUser.isActive());
        verify(userService).save(testUser);
    }

    @Test
    void activate_alreadyActive_throwsException() {
        testUser.setActive(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        assertThrows(InvalidOperationException.class, () ->
                traineeEntityService.activate("John.Doe"));
    }

    @Test
    void deactivate_success() {
        testUser.setActive(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        traineeEntityService.deactivate("John.Doe");

        assertFalse(testUser.isActive());
        verify(userService).save(testUser);
    }

    @Test
    void deactivate_alreadyInactive_throwsException() {
        testUser.setActive(false);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        assertThrows(InvalidOperationException.class, () ->
                traineeEntityService.deactivate("John.Doe"));
    }


    @Test
    void findByUsername_success() {
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        Optional<Trainee> result = traineeEntityService.findByUsername("John.Doe");

        assertTrue(result.isPresent());
        assertEquals("John.Doe", result.get().getUser().getUsername());
    }

    @Test
    void updateProfile_success() {
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeEntityService.updateProfile("John.Doe", "Jane", "Smith", new Date(), "456 Oak Ave", true);

        assertEquals("Jane", result.getUser().getFirstName());
        assertEquals("Smith", result.getUser().getLastName());
        verify(traineeRepository).save(testTrainee);
    }

    @Test
    void deleteByUsername_success() {
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        traineeEntityService.deleteByUsername("John.Doe");

        verify(traineeRepository).delete(testTrainee);
    }

    @Test
    void deleteByUsername_notFound_throwsException() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () ->
                traineeEntityService.deleteByUsername("unknown"));
    }


    @Test
    void getTrainings_success() {
        when(traineeRepository.existsByUserUsername("John.Doe")).thenReturn(true);
        when(trainingRepository.findTraineeTrainingsByCriteria(anyString(), any(), any(), any(), any()))
                .thenReturn(List.of(new Training()));

        List<Training> result = traineeEntityService.getTrainings("John.Doe", null, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void getTrainings_traineeNotFound_throwsException() {
        when(traineeRepository.existsByUserUsername("unknown")).thenReturn(false);

        assertThrows(TraineeNotFoundException.class, () ->
                traineeEntityService.getTrainings("unknown", null, null, null, null));
    }

    @Test
    void getUnassignedTrainers_success() {
        when(traineeRepository.existsByUserUsername("John.Doe")).thenReturn(true);
        when(trainerRepository.findTrainersNotAssignedToTrainee("John.Doe"))
                .thenReturn(List.of(new Trainer()));

        List<Trainer> result = traineeEntityService.getUnassignedTrainers("John.Doe");

        assertEquals(1, result.size());
    }

    @Test
    void updateTrainersList_success() {
        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("trainer1");
        trainer.setUser(trainerUser);

        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUserUsernameIn(List.of("trainer1"))).thenReturn(List.of(trainer));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        Trainee result = traineeEntityService.updateTrainersList("John.Doe", List.of("trainer1"));

        assertNotNull(result);
        verify(traineeRepository).save(testTrainee);
    }

    @Test
    void isActive_returnsTrue() {
        testUser.setActive(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        assertTrue(traineeEntityService.isActive("John.Doe"));
    }

    @Test
    void isActive_returnsFalse() {
        testUser.setActive(false);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        assertFalse(traineeEntityService.isActive("John.Doe"));
    }
}

