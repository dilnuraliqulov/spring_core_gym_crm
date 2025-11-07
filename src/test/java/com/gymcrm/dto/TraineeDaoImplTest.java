package com.gymcrm.dto;

import com.gymcrm.dao.impl.TraineeDaoImpl;
import com.gymcrm.model.Trainee;
import com.gymcrm.storage.TraineeStorage;
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
class TraineeDaoImplTest {

    @Mock
    private TraineeStorage traineeStorage;

    @InjectMocks
    private TraineeDaoImpl traineeDao;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("Toshmat");
        trainee.setLastName("Eshmatov");
    }

    @Test
    void testSave() {
        when(traineeStorage.put(trainee.getId(), trainee)).thenReturn(trainee);

        Trainee result = traineeDao.save(trainee);

        assertNotNull(result);
        assertEquals("Toshmat", result.getFirstName());
        verify(traineeStorage, times(1)).put(trainee.getId(), trainee);
    }

    @Test
    void testFindById() {
        when(traineeStorage.get(1L)).thenReturn(trainee);

        Optional<Trainee> result = traineeDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Eshmatov", result.get().getLastName());
        verify(traineeStorage).get(1L);
    }

    @Test
    void testFindAll() {
        Map<Long, Trainee> map = new HashMap<>();
        map.put(trainee.getId(), trainee);
        when(traineeStorage.values()).thenReturn(map.values());

        List<Trainee> result = traineeDao.findAll();

        assertEquals(1, result.size());
        assertEquals("Toshmat", result.get(0).getFirstName());
        verify(traineeStorage).values();
    }

    @Test
    void testDeleteById() {
        traineeDao.deleteById(1L);
        verify(traineeStorage, times(1)).remove(1L);
    }
}
