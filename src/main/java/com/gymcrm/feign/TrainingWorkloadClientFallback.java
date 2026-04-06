package com.gymcrm.feign;

import com.gymcrm.dto.request.TrainerWorkloadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainingWorkloadClientFallback implements FallbackFactory<TrainingWorkloadClient> {

    @Override
    public TrainingWorkloadClient create(Throwable cause) {
        return new TrainingWorkloadClient() {
            @Override
            public ResponseEntity<Void> updateWorkload(TrainerWorkloadRequest request, String token, String transactionId) {
                log.error("Circuit breaker fallback: Failed to update workload for trainer: {} | Transaction: {} | Cause: {}",
                        request.getTrainerUsername(), transactionId, cause.getMessage());
                log.warn("Training workload update will be retried later. Action: {}, Trainer: {}, Date: {}",
                        request.getActionType(), request.getTrainerUsername(), request.getTrainingDate());

                return ResponseEntity.ok().build();
            }
        };
    }
}

