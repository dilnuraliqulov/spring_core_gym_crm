package com.gymcrm.dao.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainer;
import com.gymcrm.storage.TrainerStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainerDaoImpl implements GenericDao<Trainer> {

    private final TrainerStorage trainerStorage;

    @Override
    public Trainer save(Trainer trainer) {
        log.info("DAO:Saving trainer: {}", trainer);
        trainerStorage.put(trainer.getId(), trainer); // no getter
        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return List.copyOf(trainerStorage.values());
    }

    @Override
    public void deleteById(Long id) {
        log.warn("DAO:Delete operation not supported for trainers");
        throw new UnsupportedOperationException("Deleting trainers is not supported");
    }
}
