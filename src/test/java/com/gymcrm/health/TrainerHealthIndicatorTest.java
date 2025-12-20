package com.gymcrm.health;

import com.gymcrm.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerHealthIndicatorTest {

    private TrainerRepository trainerRepository;
    private TrainerHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        trainerRepository = mock(TrainerRepository.class);
        healthIndicator = new TrainerHealthIndicator(trainerRepository);
    }

    @Test
    void testHealthUp() {
        when(trainerRepository.count()).thenReturn(3L);

        Health health = healthIndicator.health();
        assertEquals("UP", health.getStatus().getCode());
        assertEquals(3L, health.getDetails().get("activeTrainersCount"));
    }

    @Test
    void testHealthDown() {
        when(trainerRepository.count()).thenReturn(0L);

        Health health = healthIndicator.health();
        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals(0L, health.getDetails().get("activeTrainersCount"));
        assertEquals("No trainers found!", health.getDetails().get("message"));
    }
}
