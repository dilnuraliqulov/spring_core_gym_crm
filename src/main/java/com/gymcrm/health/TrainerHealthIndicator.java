package com.gymcrm.health;

import com.gymcrm.repository.TrainerRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("trainerHealth")
public class TrainerHealthIndicator implements HealthIndicator {

    private final TrainerRepository trainerRepository;

    public TrainerHealthIndicator(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Health health() {
        long count = trainerRepository.count();
        if (count > 0) {
            return Health.up().withDetail("activeTrainersCount", count).build();
        } else {
            return Health.down()
                    .withDetail("activeTrainersCount", count)
                    .withDetail("message", "No trainers found!")
                    .build();
        }
    }
}
