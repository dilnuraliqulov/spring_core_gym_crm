package com.gymcrm.health;

import com.gymcrm.repository.TraineeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActiveTraineeHealthIndicatorTest {

    private TraineeRepository traineeRepository;
    private ActiveTraineeHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        traineeRepository = mock(TraineeRepository.class);
        healthIndicator = new ActiveTraineeHealthIndicator(traineeRepository);
    }

    @Test
    void testHealthUp() {
        when(traineeRepository.count()).thenReturn(5L);

        Health health = healthIndicator.health();
        assertEquals("UP", health.getStatus().getCode());
        assertEquals(5L, health.getDetails().get("activeTraineesCount"));
    }

    @Test
    void testHealthDown() {
        when(traineeRepository.count()).thenReturn(0L);

        Health health = healthIndicator.health();
        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals(0L, health.getDetails().get("activeTraineesCount"));
        assertEquals("No trainees found!", health.getDetails().get("message"));
    }
}
