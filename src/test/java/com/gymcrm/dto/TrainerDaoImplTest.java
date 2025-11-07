package com.gymcrm.dto;

import com.gymcrm.dao.impl.TrainerDaoImpl;
import com.gymcrm.model.Trainer;
import com.gymcrm.storage.TrainerStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDaoImplTest {

    @Mock
    private TrainerStorage trainerStorage;

    @InjectMocks
    private TrainerDaoImpl trainerDao;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setSpecialization("Yoga");
    }

    @Test
    void testSave() {
        when(trainerStorage.put(trainer.getId(), trainer)).thenReturn(trainer);

        Trainer result = trainerDao.save(trainer);

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        assertEquals("Yoga", result.getSpecialization());
        verify(trainerStorage, times(1)).put(trainer.getId(), trainer);
    }

    @Test
    void testFindById() {
        when(trainerStorage.get(1L)).thenReturn(trainer);

        Optional<Trainer> result = trainerDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Smith", result.get().getLastName());
        verify(trainerStorage).get(1L);
    }

    @Test
    void testFindAll() {
        Map<Long, Trainer> map = new HashMap<>();
        map.put(trainer.getId(), trainer);
        when(trainerStorage.values()).thenReturn(map.values());

        List<Trainer> result = trainerDao.findAll();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(trainerStorage).values();
    }

    @Test
    void testDeleteByIdThrowsException() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> trainerDao.deleteById(1L));

        assertEquals("Deleting trainers is not supported", exception.getMessage());
    }
}
