package com.gymcrm.service.impl;

import com.gymcrm.dto.request.TrainerWorkloadRequest;
import com.gymcrm.dto.request.TrainerWorkloadRequest.ActionType;
import com.gymcrm.entity.Training;
import com.gymcrm.feign.TrainingWorkloadClient;
import com.gymcrm.security.jwt.JwtService;
import com.gymcrm.service.WorkloadNotificationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadNotificationServiceImpl implements WorkloadNotificationService {

    private final TrainingWorkloadClient workloadClient;
    private final JwtService jwtService;

    private static final String TRANSACTION_ID = "transactionId";


    @Override
    @Async
    @CircuitBreaker(name = "workloadService", fallbackMethod = "notifyWorkloadServiceFallback")
    public void notifyWorkloadService(Training training, ActionType actionType) {
        String transactionId = MDC.get(TRANSACTION_ID);
        if (transactionId == null) {
            transactionId = java.util.UUID.randomUUID().toString();
        }

        log.info("Notifying workload service for trainer: {} | Action: {} | Transaction: {}",
                training.getTrainer().getUser().getUsername(), actionType, transactionId);

        TrainerWorkloadRequest request = buildWorkloadRequest(training, actionType);

        // Generate internal service token for microservice communication
        String serviceToken = "Bearer " + jwtService.generateToken("gym-crm-service");

        try {
            workloadClient.updateWorkload(request, serviceToken, transactionId);
            log.info("Successfully notified workload service for trainer: {} | Transaction: {}",
                    training.getTrainer().getUser().getUsername(), transactionId);
        } catch (Exception e) {
            log.error("Failed to notify workload service for trainer: {} | Transaction: {} | Error: {}",
                    training.getTrainer().getUser().getUsername(), transactionId, e.getMessage());
            throw e;
        }
    }

    private TrainerWorkloadRequest buildWorkloadRequest(Training training, ActionType actionType) {
        return TrainerWorkloadRequest.builder()
                .trainerUsername(training.getTrainer().getUser().getUsername())
                .trainerFirstName(training.getTrainer().getUser().getFirstName())
                .trainerLastName(training.getTrainer().getUser().getLastName())
                .isActive(training.getTrainer().getUser().isActive())
                .trainingDate(convertToLocalDate(training.getTrainingDate()))
                .trainingDuration(training.getDurationOfTraining())
                .actionType(actionType)
                .build();
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @SuppressWarnings("unused")
    private void notifyWorkloadServiceFallback(Training training, ActionType actionType, Throwable throwable) {
        log.error("Circuit breaker fallback triggered for workload notification | Trainer: {} | Action: {} | Error: {}",
                training.getTrainer().getUser().getUsername(), actionType, throwable.getMessage());
        // In a real scenario, you might queue this for later retry
        log.warn("Training workload update for {} will need to be retried manually or through a scheduled job",
                training.getTrainer().getUser().getUsername());
    }
}

