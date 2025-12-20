package com.gymcrm.controller;

import com.gymcrm.dto.response.TrainingTypeResponse;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTrainingTypes_Success() {
        TrainingType t1 = new TrainingType();
        t1.setId(1L);
        t1.setTrainingTypeName("Yoga");

        TrainingType t2 = new TrainingType();
        t2.setId(2L);
        t2.setTrainingTypeName("Pilates");

        when(trainingTypeService.findAll()).thenReturn(List.of(t1, t2));

        ResponseEntity<List<TrainingTypeResponse>> response = trainingTypeController.getAllTrainingTypes();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }
}
