package com.gymcrm.controller;

import com.gymcrm.dto.response.TrainingTypeResponse;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor
@Tag(name = "Training Type", description = "Training types reference data APIs")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @Operation(summary = "Get all training types", description = "Retrieves list of all available training types (no authentication required)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes() {
        log.info("Getting all training types");

        List<TrainingType> trainingTypes = trainingTypeService.findAll();
        List<TrainingTypeResponse> response = trainingTypes.stream()
                .map(tt -> new TrainingTypeResponse(tt.getId(), tt.getTrainingTypeName()))
                .collect(Collectors.toList());

        log.info("Found {} training types", response.size());
        return ResponseEntity.ok(response);
    }
}

