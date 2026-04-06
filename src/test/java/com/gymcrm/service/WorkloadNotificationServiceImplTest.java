package com.gymcrm.service;


import com.gymcrm.dto.message.TrainingWorkloadMessage;
import com.gymcrm.dto.request.TrainerWorkloadRequest.ActionType;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.User;
import com.gymcrm.entity.Training;
import com.gymcrm.messaging.TrainingWorkloadMessageProducer;
import com.gymcrm.service.impl.WorkloadNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.MDC;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkloadNotificationServiceImplTest {

    @Mock
    private TrainingWorkloadMessageProducer messageProducer;

    @InjectMocks
    private WorkloadNotificationServiceImpl workloadNotificationService;

    private Training training;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Dummy user
        User user = new User();
        user.setUsername("john_doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);

        // Dummy trainer
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        // Dummy training
        training = new Training();
        training.setTrainer(trainer);
        training.setTrainingDate(new Date());
        training.setDurationOfTraining(60);
    }

    @Test
    void testNotifyWorkloadService_sendsMessage() {
        // Set a transactionId in MDC to test logging context usage
        MDC.put("transactionId", "test-tx-id");

        // Call the service
        workloadNotificationService.notifyWorkloadService(training, ActionType.ADD);

        // Verify that the message producer was called with correct message
        ArgumentCaptor<TrainingWorkloadMessage> messageCaptor = ArgumentCaptor.forClass(TrainingWorkloadMessage.class);
        verify(messageProducer, times(1)).sendWorkloadMessage(messageCaptor.capture());

        TrainingWorkloadMessage capturedMessage = messageCaptor.getValue();
        assertEquals("john_doe", capturedMessage.getTrainerUsername());
        assertEquals("John", capturedMessage.getTrainerFirstName());
        assertEquals("Doe", capturedMessage.getTrainerLastName());
        assertTrue(capturedMessage.getIsActive());
        assertEquals(60, capturedMessage.getTrainingDuration());
        assertEquals(ActionType.ADD, capturedMessage.getActionType());
        assertEquals("test-tx-id", capturedMessage.getTransactionId());
    }

    @Test
    void testNotifyWorkloadService_generatesTransactionIdIfMissing() {
        // Clear MDC to simulate missing transactionId
        MDC.clear();

        // Call the service
        workloadNotificationService.notifyWorkloadService(training, ActionType.DELETE);

        // Verify that the message producer was called
        ArgumentCaptor<TrainingWorkloadMessage> messageCaptor = ArgumentCaptor.forClass(TrainingWorkloadMessage.class);
        verify(messageProducer, times(1)).sendWorkloadMessage(messageCaptor.capture());

        TrainingWorkloadMessage capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage.getTransactionId());
        assertEquals(ActionType.DELETE, capturedMessage.getActionType());
    }

    @Test
    void testNotifyWorkloadService_handlesExceptionGracefully() {
        // Set a transactionId in MDC
        MDC.put("transactionId", "test-tx-id");

        // Simulate exception from message producer
        doThrow(new RuntimeException("ActiveMQ connection failed"))
                .when(messageProducer).sendWorkloadMessage(any(TrainingWorkloadMessage.class));

        // Should not throw exception - graceful handling
        assertDoesNotThrow(() ->
            workloadNotificationService.notifyWorkloadService(training, ActionType.ADD)
        );

        // Verify that the message producer was called
        verify(messageProducer, times(1)).sendWorkloadMessage(any(TrainingWorkloadMessage.class));
    }
}