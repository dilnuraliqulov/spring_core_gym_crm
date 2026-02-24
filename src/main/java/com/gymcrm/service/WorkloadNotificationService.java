package com.gymcrm.service;

import com.gymcrm.entity.Training;
import com.gymcrm.dto.request.TrainerWorkloadRequest.ActionType;

/**
 * Service for notifying the training workload microservice about training changes.
 */
public interface WorkloadNotificationService {

    /**
     * Notify the workload service about a training change.
     * @param training The training that was added or deleted
     * @param actionType ADD or DELETE action
     */
    void notifyWorkloadService(Training training, ActionType actionType);
}

