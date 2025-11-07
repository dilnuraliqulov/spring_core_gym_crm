package com.gymcrm.facade;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
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
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");

        training = new Training();
        training.setId(1L);
        training.setTrainingName("Yoga");
    }

    // Trainee Tests
    @Test
    void testCreateTrainee() {
        when(traineeService.save(trainee)).thenReturn(trainee);

        Trainee result = gymFacade.createTrainee(trainee);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(traineeService, times(1)).save(trainee);
    }

    @Test
    void testUpdateTrainee() {
        when(traineeService.save(trainee)).thenReturn(trainee);

        Trainee result = gymFacade.updateTrainee(trainee);

        assertNotNull(result);
        assertEquals("Doe", result.getLastName());
        verify(traineeService, times(1)).save(trainee);
    }

    @Test
    void testDeleteTrainee() {
        gymFacade.deleteTrainee(1L);
        verify(traineeService, times(1)).deleteById(1L);
    }

    @Test
    void testGetTraineeById() {
        when(traineeService.findById(1L)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = gymFacade.getTraineeById(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        verify(traineeService, times(1)).findById(1L);
    }

    @Test
    void testGetAllTrainees() {
        List<Trainee> trainees = List.of(trainee);
        when(traineeService.findAll()).thenReturn(trainees);

        List<Trainee> result = gymFacade.getAllTrainees();

        assertEquals(1, result.size());
        verify(traineeService, times(1)).findAll();
    }

    // Trainer Tests
    @Test
    void testCreateTrainer() {
        when(trainerService.save(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer(trainer);

        assertNotNull(result);
        verify(trainerService).save(trainer);
    }

    @Test
    void testUpdateTrainer() {
        when(trainerService.save(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.updateTrainer(trainer);

        assertNotNull(result);
        verify(trainerService).save(trainer);
    }

    @Test
    void testGetTrainerById() {
        when(trainerService.findById(1L)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = gymFacade.getTrainerById(1L);

        assertTrue(result.isPresent());
        verify(trainerService).findById(1L);
    }

    @Test
    void testGetAllTrainers() {
        List<Trainer> trainers = List.of(trainer);
        when(trainerService.findAll()).thenReturn(trainers);

        List<Trainer> result = gymFacade.getAllTrainers();

        assertEquals(1, result.size());
        verify(trainerService).findAll();
    }

    // Training Tests
    @Test
    void testCreateTraining() {
        when(trainingService.save(training)).thenReturn(training);

        Training result = gymFacade.createTraining(training);

        assertNotNull(result);
        verify(trainingService).save(training);
    }

    @Test
    void testGetTrainingById() {
        when(trainingService.findById(1L)).thenReturn(Optional.of(training));

        Optional<Training> result = gymFacade.getTrainingById(1L);

        assertTrue(result.isPresent());
        verify(trainingService).findById(1L);
    }

    @Test
    void testGetAllTrainings() {
        List<Training> trainings = List.of(training);
        when(trainingService.findAll()).thenReturn(trainings);

        List<Training> result = gymFacade.getAllTrainings();

        assertEquals(1, result.size());
        verify(trainingService).findAll();
    }
}
