package com.gymcrm.service;

import com.gymcrm.entity.*;
import com.gymcrm.exception.TraineeNotFoundException;
import com.gymcrm.exception.TrainerNotFoundException;
import com.gymcrm.exception.TrainingTypeNotFoundException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.repository.TraineeRepository;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.TrainingTypeRepository;
import com.gymcrm.service.impl.TrainingEntityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingEntityServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingEntityServiceImpl trainingEntityService;

    private Trainee testTrainee;
    private Trainer testTrainer;
    private TrainingType testTrainingType;
    private Training testTraining;
    private Date futureDate;

    @BeforeEach
    void setUp() {
        User traineeUser = new User();
        traineeUser.setId(1L);
        traineeUser.setUsername("trainee.user");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");

        User trainerUser = new User();
        trainerUser.setId(2L);
        trainerUser.setUsername("trainer.user");
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");

        testTrainee = new Trainee();
        testTrainee.setId(1L);
        testTrainee.setUser(traineeUser);

        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setUser(trainerUser);

        testTrainingType = new TrainingType();
        testTrainingType.setId(1L);
        testTrainingType.setTrainingTypeName("Yoga");

        futureDate = new Date(System.currentTimeMillis() + 86400000); // Tomorrow

        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setTrainee(testTrainee);
        testTraining.setTrainer(testTrainer);
        testTraining.setTrainingName("Morning Yoga");
        testTraining.setTrainingType(testTrainingType);
        testTraining.setTrainingDate(futureDate);
        testTraining.setDurationOfTraining(60);
    }

    // ==================== Add Training Tests ====================

    @Test
    void addTraining_success() {
        when(traineeRepository.findByUserUsername("trainee.user")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUserUsername("trainer.user")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.save(any(Training.class))).thenAnswer(i -> {
            Training t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        Training result = trainingEntityService.addTraining(
                "trainee.user", "trainer.user", "Morning Yoga", 1L, futureDate, 60);

        assertNotNull(result);
        assertEquals("Morning Yoga", result.getTrainingName());
        assertEquals(60, result.getDurationOfTraining());
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void addTraining_traineeNotFound_throwsException() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () ->
                trainingEntityService.addTraining("unknown", "trainer.user", "Yoga", 1L, futureDate, 60));
    }

    @Test
    void addTraining_trainerNotFound_throwsException() {
        when(traineeRepository.findByUserUsername("trainee.user")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () ->
                trainingEntityService.addTraining("trainee.user", "unknown", "Yoga", 1L, futureDate, 60));
    }

    @Test
    void addTraining_trainingTypeNotFound_throwsException() {
        when(traineeRepository.findByUserUsername("trainee.user")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUserUsername("trainer.user")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class, () ->
                trainingEntityService.addTraining("trainee.user", "trainer.user", "Yoga", 999L, futureDate, 60));
    }

    // ==================== Validation Tests ====================

    @Test
    void addTraining_blankTraineeName_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                trainingEntityService.addTraining("", "trainer.user", "Yoga", 1L, futureDate, 60));
    }

    @Test
    void addTraining_blankTrainerName_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                trainingEntityService.addTraining("trainee.user", "", "Yoga", 1L, futureDate, 60));
    }

    @Test
    void addTraining_blankTrainingName_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                trainingEntityService.addTraining("trainee.user", "trainer.user", "", 1L, futureDate, 60));
    }

    @Test
    void addTraining_nullTrainingTypeId_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                trainingEntityService.addTraining("trainee.user", "trainer.user", "Yoga", null, futureDate, 60));
    }

    @Test
    void addTraining_nullDate_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                trainingEntityService.addTraining("trainee.user", "trainer.user", "Yoga", 1L, null, 60));
    }

    @Test
    void addTraining_zeroDuration_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                trainingEntityService.addTraining("trainee.user", "trainer.user", "Yoga", 1L, futureDate, 0));
    }

    @Test
    void addTraining_negativeDuration_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                trainingEntityService.addTraining("trainee.user", "trainer.user", "Yoga", 1L, futureDate, -10));
    }

    // ==================== Find Operations Tests ====================

    @Test
    void findTrainingById_success() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(testTraining));

        Optional<Training> result = trainingEntityService.findTrainingById(1L);

        assertTrue(result.isPresent());
        assertEquals("Morning Yoga", result.get().getTrainingName());
    }

    @Test
    void findTrainingById_notFound() {
        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Training> result = trainingEntityService.findTrainingById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAllTrainings_success() {
        when(trainingRepository.findAll()).thenReturn(List.of(testTraining));

        List<Training> result = trainingEntityService.findAllTrainings();

        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
    }

    @Test
    void findAllTrainings_empty() {
        when(trainingRepository.findAll()).thenReturn(List.of());

        List<Training> result = trainingEntityService.findAllTrainings();

        assertTrue(result.isEmpty());
    }
}

