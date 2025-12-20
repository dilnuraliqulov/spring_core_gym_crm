package com.gymcrm.metrics;

import com.gymcrm.repository.TrainerRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TrainerMetricsTest {

    private TrainerRepository trainerRepository;
    private SimpleMeterRegistry meterRegistry;
    private TrainerMetrics trainerMetrics;

    @BeforeEach
    void setUp() {
        trainerRepository = mock(TrainerRepository.class);
        meterRegistry = new SimpleMeterRegistry();
        trainerMetrics = new TrainerMetrics(trainerRepository, meterRegistry);
    }

    @Test
    void testGaugeRegistered() {
        when(trainerRepository.count()).thenReturn(7L);

        trainerMetrics.init();

        Double value = meterRegistry.get("gymcrm.trainers.total").gauge().value();
        assertEquals(7.0, value);
    }
}
