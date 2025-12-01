package com.gymcrm.repository;

import com.gymcrm.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByTraineeId(Long traineeId);

    List<Training> findByTrainerId(Long trainerId);

    List<Training> findByTrainingTypeId(Long trainingTypeId);

    // Find trainings scheduled on a specific date
    List<Training> findByTrainingDate(Date trainingDate);
}
