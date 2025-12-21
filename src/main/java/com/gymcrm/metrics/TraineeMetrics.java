package com.gymcrm.metrics;

import com.gymcrm.repository.TraineeRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TraineeMetrics {

    private final TraineeRepository traineeRepository;
    private final MeterRegistry meterRegistry;

    public TraineeMetrics(TraineeRepository traineeRepository, MeterRegistry meterRegistry) {
        this.traineeRepository = traineeRepository;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        Gauge.builder("gymcrm.trainees.total", traineeRepository, TraineeRepository::count)
                .description("Total number of trainees in system")
                .register(meterRegistry);
    }
}
