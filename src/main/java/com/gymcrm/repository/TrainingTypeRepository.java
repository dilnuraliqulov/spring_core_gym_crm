package com.gymcrm.repository;

import com.gymcrm.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);

    boolean existsByTrainingTypeName(String trainingTypeName);
}
