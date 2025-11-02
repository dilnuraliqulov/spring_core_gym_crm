package com.gymcrm.dao.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainer;
import com.gymcrm.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainerDaoImpl implements GenericDao<Trainer> {

    private final InMemoryStorage storage;

    @Override
    public Trainer save(Trainer trainer) {
        log.info("Saving trainer: {}", trainer);
        storage.getTrainerStorage().put(trainer.getId(), trainer);
        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        log.info("Fetching trainer with id: {}", id);
        return Optional.ofNullable(storage.getTrainerStorage().get(id));
    }

    @Override
    public List<Trainer> findAll() {
        log.info("Fetching all trainers");
        return List.copyOf(storage.getTrainerStorage().values());
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting trainer with id: {}", id);
        storage.getTrainerStorage().remove(id);
    }
}
