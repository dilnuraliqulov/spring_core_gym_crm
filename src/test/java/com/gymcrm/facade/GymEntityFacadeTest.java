package com.gymcrm.facade;

import com.gymcrm.entity.*;
import com.gymcrm.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymEntityFacadeTest {

    @Mock
    private TraineeEntityService traineeEntityService;

    @Mock
    private TrainerEntityService trainerEntityService;

    @Mock
    private TrainingEntityService trainingEntityService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private AuthenticationService traineeAuthService;

    @Mock
    private AuthenticationService trainerAuthService;

    @Mock
    private ActivationService traineeActivationService;

    @Mock
    private ActivationService trainerActivationService;

    private GymEntityFacade gymEntityFacade;

    private Trainee testTrainee;
    private Trainer testTrainer;
    private Training testTraining;
    private char[] testPassword;

    @BeforeEach
    void setUp() {
        gymEntityFacade = new GymEntityFacade(
                traineeEntityService,
                trainerEntityService,
                trainingEntityService,
                trainingTypeService,
                traineeAuthService,
                trainerAuthService,
                traineeActivationService,
                trainerActivationService
        );

        User traineeUser = new User();
        traineeUser.setUsername("trainee.user");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");

        testTrainee = new Trainee();
        testTrainee.setId(1L);
        testTrainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setUsername("trainer.user");
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");

        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setUser(trainerUser);

        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setTrainingName("Morning Yoga");

        testPassword = "password123".toCharArray();
    }


    @Test
    void createTraineeProfile_success() {
        when(traineeEntityService.createProfile("John", "Doe", null, "123 Main St"))
                .thenReturn(testTrainee);

        Trainee result = gymEntityFacade.createTraineeProfile("John", "Doe", null, "123 Main St");

        assertNotNull(result);
        assertEquals("trainee.user", result.getUser().getUsername());
        verify(traineeEntityService).createProfile("John", "Doe", null, "123 Main St");
    }


    @Test
    void authenticateTrainee_success() {
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);

        boolean result = gymEntityFacade.authenticateTrainee("trainee.user", testPassword);

        assertTrue(result);
        verify(traineeAuthService).authenticate("trainee.user", testPassword);
    }

    @Test
    void getTraineeByUsername_authenticatesFirst() {
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);
        when(traineeEntityService.findByUsername("trainee.user")).thenReturn(Optional.of(testTrainee));

        Optional<Trainee> result = gymEntityFacade.getTraineeByUsername("trainee.user", testPassword);

        assertTrue(result.isPresent());
        verify(traineeAuthService).authenticate("trainee.user", testPassword);
        verify(traineeEntityService).findByUsername("trainee.user");
    }

    @Test
    void changeTraineePassword_callsAuthService() {
        char[] currentPassword = "current".toCharArray();
        char[] newPassword = "newPassword".toCharArray();

        gymEntityFacade.changeTraineePassword("trainee.user", currentPassword, newPassword);

        verify(traineeAuthService).changePassword("trainee.user", currentPassword, newPassword);
    }


    @Test
    void updateTraineeProfile_authenticatesFirst() {
        Date dob = new Date();
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);
        when(traineeEntityService.updateProfile("trainee.user", "John", "Doe", dob, "123 Main St", true))
                .thenReturn(testTrainee);

        Trainee result = gymEntityFacade.updateTraineeProfile(
                "trainee.user", testPassword, "John", "Doe", dob, "123 Main St", true);

        assertNotNull(result);
        verify(traineeAuthService).authenticate("trainee.user", testPassword);
    }


    @Test
    void activateTrainee_authenticatesFirst() {
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);

        gymEntityFacade.activateTrainee("trainee.user", testPassword);

        verify(traineeAuthService).authenticate("trainee.user", testPassword);
        verify(traineeActivationService).activate("trainee.user");
    }

    @Test
    void deactivateTrainee_authenticatesFirst() {
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);

        gymEntityFacade.deactivateTrainee("trainee.user", testPassword);

        verify(traineeAuthService).authenticate("trainee.user", testPassword);
        verify(traineeActivationService).deactivate("trainee.user");
    }


    @Test
    void deleteTraineeByUsername_authenticatesFirst() {
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);

        gymEntityFacade.deleteTraineeByUsername("trainee.user", testPassword);

        verify(traineeAuthService).authenticate("trainee.user", testPassword);
        verify(traineeEntityService).deleteByUsername("trainee.user");
    }


    @Test
    void getTraineeTrainings_authenticatesFirst() {
        Date fromDate = new Date();
        Date toDate = new Date();
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);
        when(traineeEntityService.getTrainings("trainee.user", fromDate, toDate, "trainer", "Yoga"))
                .thenReturn(List.of(testTraining));

        List<Training> result = gymEntityFacade.getTraineeTrainings(
                "trainee.user", testPassword, fromDate, toDate, "trainer", "Yoga");

        assertEquals(1, result.size());
        verify(traineeAuthService).authenticate("trainee.user", testPassword);
    }

    @Test
    void getUnassignedTrainers_authenticatesFirst() {
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);
        when(traineeEntityService.getUnassignedTrainers("trainee.user")).thenReturn(List.of(testTrainer));

        List<Trainer> result = gymEntityFacade.getUnassignedTrainers("trainee.user", testPassword);

        assertEquals(1, result.size());
        verify(traineeAuthService).authenticate("trainee.user", testPassword);
    }

    @Test
    void updateTraineeTrainersList_authenticatesFirst() {
        List<String> trainerUsernames = List.of("trainer1", "trainer2");
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);
        when(traineeEntityService.updateTrainersList("trainee.user", trainerUsernames)).thenReturn(testTrainee);

        Trainee result = gymEntityFacade.updateTraineeTrainersList("trainee.user", testPassword, trainerUsernames);

        assertNotNull(result);
        verify(traineeAuthService).authenticate("trainee.user", testPassword);
    }

    @Test
    void createTrainerProfile_success() {
        when(trainerEntityService.createProfile("Jane", "Smith", 1L)).thenReturn(testTrainer);

        Trainer result = gymEntityFacade.createTrainerProfile("Jane", "Smith", 1L);

        assertNotNull(result);
        assertEquals("trainer.user", result.getUser().getUsername());
        verify(trainerEntityService).createProfile("Jane", "Smith", 1L);
    }


    @Test
    void authenticateTrainer_success() {
        when(trainerAuthService.authenticate("trainer.user", testPassword)).thenReturn(true);

        boolean result = gymEntityFacade.authenticateTrainer("trainer.user", testPassword);

        assertTrue(result);
        verify(trainerAuthService).authenticate("trainer.user", testPassword);
    }

    @Test
    void getTrainerByUsername_authenticatesFirst() {
        when(trainerAuthService.authenticate("trainer.user", testPassword)).thenReturn(true);
        when(trainerEntityService.findByUsername("trainer.user")).thenReturn(Optional.of(testTrainer));

        Optional<Trainer> result = gymEntityFacade.getTrainerByUsername("trainer.user", testPassword);

        assertTrue(result.isPresent());
        verify(trainerAuthService).authenticate("trainer.user", testPassword);
    }


    @Test
    void activateTrainer_authenticatesFirst() {
        when(trainerAuthService.authenticate("trainer.user", testPassword)).thenReturn(true);

        gymEntityFacade.activateTrainer("trainer.user", testPassword);

        verify(trainerAuthService).authenticate("trainer.user", testPassword);
        verify(trainerActivationService).activate("trainer.user");
    }

    @Test
    void deactivateTrainer_authenticatesFirst() {
        when(trainerAuthService.authenticate("trainer.user", testPassword)).thenReturn(true);

        gymEntityFacade.deactivateTrainer("trainer.user", testPassword);

        verify(trainerAuthService).authenticate("trainer.user", testPassword);
        verify(trainerActivationService).deactivate("trainer.user");
    }


    @Test
    void getTrainerTrainings_authenticatesFirst() {
        Date fromDate = new Date();
        Date toDate = new Date();
        when(trainerAuthService.authenticate("trainer.user", testPassword)).thenReturn(true);
        when(trainerEntityService.getTrainings("trainer.user", fromDate, toDate, "trainee"))
                .thenReturn(List.of(testTraining));

        List<Training> result = gymEntityFacade.getTrainerTrainings(
                "trainer.user", testPassword, fromDate, toDate, "trainee");

        assertEquals(1, result.size());
        verify(trainerAuthService).authenticate("trainer.user", testPassword);
    }


    @Test
    void addTraining_authenticatesTraineeFirst() {
        Date trainingDate = new Date();
        when(traineeAuthService.authenticate("trainee.user", testPassword)).thenReturn(true);
        when(trainingEntityService.addTraining("trainee.user", "trainer.user", "Yoga", 1L, trainingDate, 60))
                .thenReturn(testTraining);

        Training result = gymEntityFacade.addTraining(
                "trainee.user", testPassword, "trainer.user", "Yoga", 1L, trainingDate, 60);

        assertNotNull(result);
        verify(traineeAuthService).authenticate("trainee.user", testPassword);
        verify(trainingEntityService).addTraining("trainee.user", "trainer.user", "Yoga", 1L, trainingDate, 60);
    }


    @Test
    void getAllTrainingTypes_success() {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("Yoga");
        when(trainingTypeService.findAll()).thenReturn(List.of(type));

        List<TrainingType> result = gymEntityFacade.getAllTrainingTypes();

        assertEquals(1, result.size());
        assertEquals("Yoga", result.get(0).getTrainingTypeName());
    }

    @Test
    void getTrainingTypeById_success() {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("Yoga");
        when(trainingTypeService.findById(1L)).thenReturn(Optional.of(type));

        Optional<TrainingType> result = gymEntityFacade.getTrainingTypeById(1L);

        assertTrue(result.isPresent());
        assertEquals("Yoga", result.get().getTrainingTypeName());
    }
}

