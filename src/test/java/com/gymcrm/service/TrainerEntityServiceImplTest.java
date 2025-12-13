package com.gymcrm.service;

import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.entity.User;
import com.gymcrm.exception.InvalidOperationException;
import com.gymcrm.exception.TrainerNotFoundException;
import com.gymcrm.exception.TrainingTypeNotFoundException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.TrainingTypeRepository;
import com.gymcrm.repository.UserRepository;
import com.gymcrm.service.impl.TrainerEntityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerEntityServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainerEntityServiceImpl trainerEntityService;

    private User testUser;
    private Trainer testTrainer;
    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Jane");
        testUser.setLastName("Smith");
        testUser.setUsername("Jane.Smith");
        testUser.setPassword("hashedPassword".toCharArray());
        testUser.setActive(true);

        testTrainingType = new TrainingType();
        testTrainingType.setId(1L);
        testTrainingType.setTrainingTypeName("Yoga");

        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setUser(testUser);
        testTrainer.setSpecialization(testTrainingType);
    }


    @Test
    void createProfile_success() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(testTrainingType));
        when(userService.hashPassword(any(char[].class))).thenReturn("encodedPassword".toCharArray());
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(i -> {
            Trainer t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        Trainer result = trainerEntityService.createProfile("Jane", "Smith", 1L);

        assertNotNull(result);
        assertEquals("Jane.Smith", result.getUser().getUsername());
        assertTrue(result.getUser().isActive());
        assertNotNull(result.getSpecialization());
        verify(trainerRepository).save(any(Trainer.class));
        verify(userService).hashPassword(any(char[].class));
    }

    @Test
    void createProfile_withoutSpecialization_success() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userService.hashPassword(any(char[].class))).thenReturn("encodedPassword".toCharArray());
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(i -> {
            Trainer t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        Trainer result = trainerEntityService.createProfile("Jane", "Smith", null);

        assertNotNull(result);
        assertNull(result.getSpecialization());
    }

    @Test
    void createProfile_invalidSpecializationId_throwsException() {
        when(trainingTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class, () ->
                trainerEntityService.createProfile("Jane", "Smith", 999L));
    }

    @Test
    void createProfile_withBlankFirstName_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                trainerEntityService.createProfile("", "Smith", 1L));
    }

    // ==================== Authentication Tests ====================

    @Test
    void authenticate_success() {
        when(trainerRepository.existsByUserUsername("Jane.Smith")).thenReturn(true);
        when(userService.authenticate("Jane.Smith", "password".toCharArray())).thenReturn(true);

        boolean result = trainerEntityService.authenticate("Jane.Smith", "password".toCharArray());

        assertTrue(result);
        verify(userService).authenticate("Jane.Smith", "password".toCharArray());
    }

    @Test
    void authenticate_trainerNotFound_throwsException() {
        when(trainerRepository.existsByUserUsername("unknown")).thenReturn(false);

        assertThrows(TrainerNotFoundException.class, () ->
                trainerEntityService.authenticate("unknown", "password".toCharArray()));
    }

    @Test
    void authenticate_delegatesToUserService() {
        when(trainerRepository.existsByUserUsername("Jane.Smith")).thenReturn(true);
        when(userService.authenticate("Jane.Smith", "wrongPassword".toCharArray()))
                .thenThrow(new BadCredentialsException("Invalid password"));

        assertThrows(BadCredentialsException.class, () ->
                trainerEntityService.authenticate("Jane.Smith", "wrongPassword".toCharArray()));
    }

    // ==================== Change Password Tests ====================

    @Test
    void changePassword_success() {
        when(trainerRepository.existsByUserUsername("Jane.Smith")).thenReturn(true);

        trainerEntityService.changePassword("Jane.Smith", "currentPassword".toCharArray(), "newPassword".toCharArray());

        verify(userService).changePassword("Jane.Smith", "currentPassword".toCharArray(), "newPassword".toCharArray());
    }

    @Test
    void changePassword_trainerNotFound_throwsException() {
        when(trainerRepository.existsByUserUsername("unknown")).thenReturn(false);

        assertThrows(TrainerNotFoundException.class, () ->
                trainerEntityService.changePassword("unknown", "current".toCharArray(), "new".toCharArray()));
    }

    // ==================== Activation Tests ====================

    @Test
    void activate_success() {
        testUser.setActive(false);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(testTrainer));

        trainerEntityService.activate("Jane.Smith");

        assertTrue(testUser.isActive());
        verify(userService).save(testUser);
    }

    @Test
    void activate_alreadyActive_throwsException() {
        testUser.setActive(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(testTrainer));

        assertThrows(InvalidOperationException.class, () ->
                trainerEntityService.activate("Jane.Smith"));
    }

    @Test
    void deactivate_success() {
        testUser.setActive(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(testTrainer));

        trainerEntityService.deactivate("Jane.Smith");

        assertFalse(testUser.isActive());
        verify(userService).save(testUser);
    }

    @Test
    void deactivate_alreadyInactive_throwsException() {
        testUser.setActive(false);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(testTrainer));

        assertThrows(InvalidOperationException.class, () ->
                trainerEntityService.deactivate("Jane.Smith"));
    }

    // ==================== Profile Operations Tests ====================

    @Test
    void findByUsername_success() {
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(testTrainer));

        Optional<Trainer> result = trainerEntityService.findByUsername("Jane.Smith");

        assertTrue(result.isPresent());
        assertEquals("Jane.Smith", result.get().getUser().getUsername());
    }

    @Test
    void updateProfile_success() {
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(testTrainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        Trainer result = trainerEntityService.updateProfile("Jane.Smith", "Janet", "Jones", 1L, true);

        assertEquals("Janet", result.getUser().getFirstName());
        assertEquals("Jones", result.getUser().getLastName());
        verify(trainerRepository).save(testTrainer);
    }

    // ==================== Training Operations Tests ====================

    @Test
    void getTrainings_success() {
        when(trainerRepository.existsByUserUsername("Jane.Smith")).thenReturn(true);
        when(trainingRepository.findTrainerTrainingsByCriteria(anyString(), any(), any(), any()))
                .thenReturn(List.of(new Training()));

        List<Training> result = trainerEntityService.getTrainings("Jane.Smith", null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void getTrainings_trainerNotFound_throwsException() {
        when(trainerRepository.existsByUserUsername("unknown")).thenReturn(false);

        assertThrows(TrainerNotFoundException.class, () ->
                trainerEntityService.getTrainings("unknown", null, null, null));
    }

    @Test
    void findAll_success() {
        when(trainerRepository.findAll()).thenReturn(List.of(testTrainer));

        List<Trainer> result = trainerEntityService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findById_success() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));

        Optional<Trainer> result = trainerEntityService.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void isActive_returnsTrue() {
        testUser.setActive(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(testTrainer));

        assertTrue(trainerEntityService.isActive("Jane.Smith"));
    }

    @Test
    void isActive_returnsFalse() {
        testUser.setActive(false);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(testTrainer));

        assertFalse(trainerEntityService.isActive("Jane.Smith"));
    }
}
