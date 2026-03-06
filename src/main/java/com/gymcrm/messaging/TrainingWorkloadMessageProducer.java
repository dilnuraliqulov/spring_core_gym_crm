package com.gymcrm.messaging;

import com.gymcrm.config.JmsConfig;
import com.gymcrm.dto.message.TrainingWorkloadMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingWorkloadMessageProducer {

    private final JmsTemplate jmsTemplate;

    public void sendWorkloadMessage(TrainingWorkloadMessage message) {
        log.info("Sending training workload message to queue: {} | Trainer: {} | Action: {} | TransactionId: {}",
                JmsConfig.TRAINING_WORKLOAD_QUEUE,
                message.getTrainerUsername(),
                message.getActionType(),
                message.getTransactionId());

        try {
            jmsTemplate.convertAndSend(JmsConfig.TRAINING_WORKLOAD_QUEUE, message);
            log.info("Successfully sent workload message to queue for trainer: {} | TransactionId: {}",
                    message.getTrainerUsername(), message.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to send workload message to queue | Trainer: {} | TransactionId: {} | Error: {}",
                    message.getTrainerUsername(), message.getTransactionId(), e.getMessage());
            throw new RuntimeException("Failed to send message to ActiveMQ", e);
        }
    }
}

