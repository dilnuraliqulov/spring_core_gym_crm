package com.gymcrm.metrics;

import com.gymcrm.repository.TrainingRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


@Component
public class TrainingMetrics {

    private final TrainingRepository trainingRepository;
    private final MeterRegistry meterRegistry;

    private Counter totalTrainingsCounter;

    public TrainingMetrics(TrainingRepository trainingRepository, MeterRegistry meterRegistry) {
        this.trainingRepository = trainingRepository;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        totalTrainingsCounter = Counter.builder("gymcrm.trainings.total")
                .description("Total number of trainings in system")
                .register(meterRegistry);

        totalTrainingsCounter.increment(trainingRepository.count());
    }

    public void incrementTrainings() {
        totalTrainingsCounter.increment();
    }
}
