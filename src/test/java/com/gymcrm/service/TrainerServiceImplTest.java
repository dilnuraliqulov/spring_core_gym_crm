package com.gymcrm.service;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.impl.TrainerServiceImpl;
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
class TrainerServiceImplTest {

    @Mock
    private GenericDao<Trainer> trainerDao;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Dilnur");
        trainer.setLastName("Aliqulov");
        trainer.setSpecialization("Yoga");
    }

    @Test
    void testSaveTrainer_GeneratesUsernameAndPassword() {
        when(trainerDao.findAll()).thenReturn(List.of());
        when(trainerDao.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.save(trainer);

        assertNotNull(result.getUsername(), "Username should be generated");
        assertNotNull(result.getPassword(), "Password should be generated");
        assertEquals(10, result.getPassword().length(), "Password should be 10 characters long");
        assertEquals("Dilnur", result.getFirstName());
        assertEquals("Aliqulov", result.getLastName());

        verify(trainerDao, times(1)).findAll();
        verify(trainerDao, times(1)).save(result);
    }

    @Test
    void testSaveTrainer_DuplicateUsername() {
        Trainer existing = new Trainer();
        existing.setUsername("Dilnur.Aliqulov");

        when(trainerDao.findAll()).thenReturn(List.of(existing));
        when(trainerDao.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName("Dilnur");
        newTrainer.setLastName("Aliqulov");

        Trainer result = trainerService.save(newTrainer);

        assertEquals("Dilnur.Aliqulov1", result.getUsername(), "Duplicate username should get numeric suffix");
        assertNotNull(result.getPassword());

        verify(trainerDao, times(1)).findAll();
        verify(trainerDao, times(1)).save(result);
    }

    @Test
    void testSaveTrainer_KeepsExistingUsernamePassword() {
        trainer.setUsername("ExistingUser");
        trainer.setPassword("ExistingPass");

        when(trainerDao.findAll()).thenReturn(List.of());
        when(trainerDao.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.save(trainer);

        assertEquals("ExistingUser", result.getUsername());
        assertEquals("ExistingPass", result.getPassword());

        verify(trainerDao, times(1)).findAll();
        verify(trainerDao, times(1)).save(result);
    }

    @Test
    void testFindTrainerById() {
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Aliqulov", result.get().getLastName());
        assertEquals("Yoga", result.get().getSpecialization());
        verify(trainerDao).findById(1L);
    }

    @Test
    void testFindAllTrainers() {
        List<Trainer> trainers = List.of(trainer);
        when(trainerDao.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.findAll();

        assertEquals(1, result.size());
        assertEquals("Dilnur", result.get(0).getFirstName());
        verify(trainerDao).findAll();
    }
}
