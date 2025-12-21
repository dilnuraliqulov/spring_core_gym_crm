package com.gymcrm.metrics;

import com.gymcrm.repository.TraineeRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TraineeMetricsTest {

    private TraineeRepository traineeRepository;
    private MeterRegistry meterRegistry;
    private TraineeMetrics traineeMetrics;

    @BeforeEach
    void setUp() {
        traineeRepository = mock(TraineeRepository.class);
        meterRegistry = new SimpleMeterRegistry();
        traineeMetrics = new TraineeMetrics(traineeRepository, meterRegistry);
    }

    @Test
    void testGaugeRegistered() {
        when(traineeRepository.count()).thenReturn(10L);

        traineeMetrics.init();

        Double value = meterRegistry.get("gymcrm.trainees.total").gauge().value();
        assertEquals(10.0, value);
    }
}
