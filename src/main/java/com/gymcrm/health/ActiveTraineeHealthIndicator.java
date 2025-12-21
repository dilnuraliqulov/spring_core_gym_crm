package com.gymcrm.health;

import com.gymcrm.repository.TraineeRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("activeTrainees")
public class ActiveTraineeHealthIndicator implements HealthIndicator {

    private final TraineeRepository traineeRepository;

    public ActiveTraineeHealthIndicator(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Override
    public Health health() {
        long count = traineeRepository.count();
        if (count > 0) {
            return Health.up()
                    .withDetail("activeTraineesCount", count)
                    .build();
        } else {
            return Health.down()
                    .withDetail("activeTraineesCount", count)
                    .withDetail("message", "No trainees found!")
                    .build();
        }
    }
}
