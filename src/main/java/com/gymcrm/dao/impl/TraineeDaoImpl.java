package com.gymcrm.dao.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainee;
import com.gymcrm.storage.TraineeStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TraineeDaoImpl implements GenericDao<Trainee> {

    private final TraineeStorage traineeStorage;

    @Override
    public Trainee save(Trainee trainee) {
        log.trace("DAO:Saving trainee: {}", trainee);
        traineeStorage.put(trainee.getId(), trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return List.copyOf(traineeStorage.values());
    }

    @Override
    public void deleteById(Long id) {
        log.trace("DAO:Deleting trainee with id: {}", id);
        traineeStorage.remove(id);
    }

}
