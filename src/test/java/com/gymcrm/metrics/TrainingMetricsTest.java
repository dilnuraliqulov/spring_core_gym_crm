package com.gymcrm.metrics;

import com.gymcrm.repository.TrainingRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TrainingMetricsTest {

    private TrainingRepository trainingRepository;
    private SimpleMeterRegistry meterRegistry;
    private TrainingMetrics trainingMetrics;

    @BeforeEach
    void setUp() {
        trainingRepository = mock(TrainingRepository.class);
        meterRegistry = new SimpleMeterRegistry();
        trainingMetrics = new TrainingMetrics(trainingRepository, meterRegistry);
    }

    @Test
    void testCounterInitialization() {
        when(trainingRepository.count()).thenReturn(5L);

        trainingMetrics.init();

        double value = meterRegistry.get("gymcrm.trainings.total").counter().count();
        assertEquals(5.0, value);
    }

    @Test
    void testIncrementTrainings() {
        when(trainingRepository.count()).thenReturn(5L);
        trainingMetrics.init();

        trainingMetrics.incrementTrainings();

        double value = meterRegistry.get("gymcrm.trainings.total").counter().count();
        assertEquals(6.0, value);
    }
}
