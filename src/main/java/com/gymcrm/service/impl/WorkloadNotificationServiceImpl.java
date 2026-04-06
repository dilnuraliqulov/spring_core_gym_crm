package com.gymcrm.service.impl;

import com.gymcrm.dto.message.TrainingWorkloadMessage;
import com.gymcrm.dto.request.TrainerWorkloadRequest.ActionType;
import com.gymcrm.entity.Training;
import com.gymcrm.messaging.TrainingWorkloadMessageProducer;
import com.gymcrm.service.WorkloadNotificationService;
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

    private final TrainingWorkloadMessageProducer messageProducer;

    private static final String TRANSACTION_ID = "transactionId";


    @Override
    @Async
    public void notifyWorkloadService(Training training, ActionType actionType) {
        String transactionId = MDC.get(TRANSACTION_ID);
        if (transactionId == null) {
            transactionId = java.util.UUID.randomUUID().toString();
        }

        log.info("Sending async workload message for trainer: {} | Action: {} | Transaction: {}",
                training.getTrainer().getUser().getUsername(), actionType, transactionId);

        TrainingWorkloadMessage message = buildWorkloadMessage(training, actionType, transactionId);

        try {
            messageProducer.sendWorkloadMessage(message);
            log.info("Successfully queued workload message for trainer: {} | Transaction: {}",
                    training.getTrainer().getUser().getUsername(), transactionId);
        } catch (Exception e) {
            log.error("Failed to queue workload message for trainer: {} | Transaction: {} | Error: {}",
                    training.getTrainer().getUser().getUsername(), transactionId, e.getMessage());
        }
    }

    private TrainingWorkloadMessage buildWorkloadMessage(Training training, ActionType actionType, String transactionId) {
        return TrainingWorkloadMessage.builder()
                .trainerUsername(training.getTrainer().getUser().getUsername())
                .trainerFirstName(training.getTrainer().getUser().getFirstName())
                .trainerLastName(training.getTrainer().getUser().getLastName())
                .isActive(training.getTrainer().getUser().isActive())
                .trainingDate(convertToLocalDate(training.getTrainingDate()))
                .trainingDuration(training.getDurationOfTraining())
                .actionType(actionType)
                .transactionId(transactionId)
                .build();
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}

