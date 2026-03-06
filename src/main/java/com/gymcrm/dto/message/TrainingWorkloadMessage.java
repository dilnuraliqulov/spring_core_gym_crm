package com.gymcrm.dto.message;

import com.gymcrm.dto.request.TrainerWorkloadRequest.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingWorkloadMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    private LocalDate trainingDate;
    private Integer trainingDuration;
    private ActionType actionType;
    private String transactionId;
}

