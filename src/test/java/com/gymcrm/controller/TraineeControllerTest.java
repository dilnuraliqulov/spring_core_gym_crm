package com.gymcrm.controller;

import com.gymcrm.dto.request.TraineeRegistrationRequest;
import com.gymcrm.dto.response.RegistrationResponse;
import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.User;
import com.gymcrm.service.TraineeEntityService;
import com.gymcrm.service.TrainerEntityService;
import com.gymcrm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeControllerTest {

    @Mock
    private TraineeEntityService traineeService;

    @Mock
    private TrainerEntityService trainerService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerTrainee_Success() {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("pass".toCharArray());
        trainee.setUser(user);

        when(trainerService.findByUsername(anyString())).thenReturn(Optional.empty());
        when(traineeService.createProfile(anyString(), anyString(), any(), any())).thenReturn(trainee);

        ResponseEntity<RegistrationResponse> response = traineeController.registerTrainee(request);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("john.doe", response.getBody().getUsername());
    }
}
