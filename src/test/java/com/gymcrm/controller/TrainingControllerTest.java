package com.gymcrm.controller;

import com.gymcrm.dto.request.AddTrainingRequest;
import com.gymcrm.service.TrainingEntityService;
import com.gymcrm.service.TrainingTypeService;
import com.gymcrm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingControllerTest {

    @Mock
    private TrainingEntityService trainingService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTraining_Success() {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("user1");
        request.setTrainerUsername("trainer1");
        request.setTrainingName("Yoga");
        request.setDuration(60);

        when(userService.authenticate(anyString(), any())).thenReturn(true);

        ResponseEntity<Void> response = trainingController.addTraining(request, "user1", "pass");
        assertEquals(200, response.getStatusCodeValue());
        verify(trainingService).addTraining(anyString(), anyString(), anyString(), isNull(), any(), anyInt());
    }
}
