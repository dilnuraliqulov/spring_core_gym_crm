package com.gymcrm.service_spring_core;

import com.gymcrm.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingService {
    Training save(Training training);
    Optional<Training> findById(Long id);
    List<Training> findAll();
}
