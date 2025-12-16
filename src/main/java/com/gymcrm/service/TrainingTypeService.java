package com.gymcrm.service;

import com.gymcrm.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeService {


    List<TrainingType> findAll();


    Optional<TrainingType> findById(Long id);


    Optional<TrainingType> findByName(String name);
}

