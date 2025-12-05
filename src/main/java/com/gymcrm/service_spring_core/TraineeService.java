package com.gymcrm.service_spring_core;

import com.gymcrm.model.Trainee;
import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee save(Trainee trainee);
    Optional<Trainee> findById(Long id);
    List<Trainee> findAll();
    void deleteById(Long id);
}
