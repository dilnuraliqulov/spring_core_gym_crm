package com.gymcrm.controller;

import com.gymcrm.dto.request.TrainerRegistrationRequest;
import com.gymcrm.dto.response.RegistrationResponse;
import com.gymcrm.entity.Trainer;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainerControllerTest {

    @Mock
    private TrainerEntityService trainerService;

    @Mock
    private TraineeEntityService traineeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerTrainer_Success() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");

        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("jane.smith");
        user.setPassword("pass".toCharArray());
        trainer.setUser(user);

        when(traineeService.findByUsername(anyString())).thenReturn(Optional.empty());
        when(trainerService.createProfile(anyString(), anyString(), any())).thenReturn(trainer);

        ResponseEntity<RegistrationResponse> response = trainerController.registerTrainer(request);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("jane.smith", response.getBody().getUsername());
    }
}
