package com.gymcrm.dao.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainee;
import com.gymcrm.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TraineeDaoImpl implements GenericDao<Trainee> {

    private final InMemoryStorage storage;

    @Override
    public Trainee save(Trainee trainee) {
        log.info("Saving trainee: {}", trainee);
        storage.getTraineeStorage().put(trainee.getId(), trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        log.info("Fetching trainee with id: {}", id);
        return Optional.ofNullable(storage.getTraineeStorage().get(id));
    }

    @Override
    public List<Trainee> findAll() {
        log.info("Fetching all trainees");
        return List.copyOf(storage.getTraineeStorage().values());
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting trainee with id: {}", id);
        storage.getTraineeStorage().remove(id);
    }
}
