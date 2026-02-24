package com.gymcrm.service;


import com.gymcrm.dto.request.TrainerWorkloadRequest;
import com.gymcrm.dto.request.TrainerWorkloadRequest.ActionType;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.User;
import com.gymcrm.entity.Training;
import com.gymcrm.feign.TrainingWorkloadClient;
import com.gymcrm.security.jwt.JwtService;
import com.gymcrm.service.impl.WorkloadNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.MDC;

import java.util.Date;

import static org.mockito.Mockito.*;

class WorkloadNotificationServiceImplTest {

    @Mock
    private TrainingWorkloadClient workloadClient;

    @Mock
    private JwtService jwtService;

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

        // Mock JWT token generation
        when(jwtService.generateToken(anyString())).thenReturn("dummy-token");
    }

    @Test
    void testNotifyWorkloadService_callsClient() throws Exception {
        // Set a transactionId in MDC to test logging context usage
        MDC.put("transactionId", "test-tx-id");

        // Call the service
        workloadNotificationService.notifyWorkloadService(training, ActionType.ADD);

        // Verify that the Feign client was called with correct args
        ArgumentCaptor<TrainerWorkloadRequest> requestCaptor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(workloadClient, times(1))
                .updateWorkload(requestCaptor.capture(), eq("Bearer dummy-token"), eq("test-tx-id"));

        TrainerWorkloadRequest capturedRequest = requestCaptor.getValue();
        assert capturedRequest.getTrainerUsername().equals("john_doe");
        assert capturedRequest.getActionType() == ActionType.ADD;
    }
}