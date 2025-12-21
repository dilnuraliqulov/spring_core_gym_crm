package com.gymcrm.metrics;

import com.gymcrm.repository.TrainerRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


@Component
public class TrainerMetrics {

    private final TrainerRepository trainerRepository;
    private final MeterRegistry meterRegistry;

    public TrainerMetrics(TrainerRepository trainerRepository, MeterRegistry meterRegistry) {
        this.trainerRepository = trainerRepository;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        Gauge.builder("gymcrm.trainers.total", trainerRepository, TrainerRepository::count)
                .description("Total number of trainers in system")
                .register(meterRegistry);
    }
}
