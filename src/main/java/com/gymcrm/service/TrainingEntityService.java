package com.gymcrm.service;

import com.gymcrm.entity.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface TrainingEntityService {


    Training addTraining(String traineeUsername, String trainerUsername, String trainingName,
                         Long trainingTypeId, Date trainingDate, Integer duration);


    Optional<Training> findTrainingById(Long id);

    List<Training> findAllTrainings();
}
