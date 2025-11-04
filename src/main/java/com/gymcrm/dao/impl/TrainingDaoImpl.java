package com.gymcrm.dao.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.gymcrm.storage.TrainingStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainingDaoImpl implements GenericDao<Training> {

    private final TrainingStorage trainingStorage;

    /**
     * Only supports creation of new Training.
     * Updating an existing Training is not allowed.
     */
    @Override
    public Training save(Training training) {
        if (trainingStorage.getTraningStorage().containsKey(training.getId())) {
            log.warn("Update operation not supported for trainings with id: {}", training.getId());
            throw new UnsupportedOperationException("Updating trainings is not supported");
        }
        log.info("Creating new training: {}", training);
        trainingStorage.getTraningStorage().put(training.getId(), training);
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.info("Fetching training with id: {}", id);
        return Optional.ofNullable(trainingStorage.getTraningStorage().get(id));
    }

    @Override
    public List<Training> findAll() {
        log.info("Fetching all trainings");
        return List.copyOf(trainingStorage.getTraningStorage().values());
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Delete operation not supported for trainings with id: {}", id);
        throw new UnsupportedOperationException("Deleting trainings is not supported");
    }
}
