package com.gymcrm.dao.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Training;
import com.gymcrm.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainingDaoImpl implements GenericDao<Training> {

    private final InMemoryStorage storage;

    @Override
    public Training save(Training training) {
        log.info("Saving training: {}", training);
        storage.getTrainingStorage().put(training.getId(), training);
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.info("Fetching training with id: {}", id);
        return Optional.ofNullable(storage.getTrainingStorage().get(id));
    }

    @Override
    public List<Training> findAll() {
        log.info("Fetching all trainings");
        return List.copyOf(storage.getTrainingStorage().values());
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting training with id: {}", id);
        storage.getTrainingStorage().remove(id);
    }
}
