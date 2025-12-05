package com.gymcrm.service_spring_core;

import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer save(Trainer trainer);
    Optional<Trainer> findById(Long id);
    List<Trainer> findAll();
}
