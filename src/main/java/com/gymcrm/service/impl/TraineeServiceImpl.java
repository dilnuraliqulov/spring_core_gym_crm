package com.gymcrm.service.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainee;
import com.gymcrm.service.TraineeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final GenericDao<Trainee> traineeDao;

    @Override
    public Trainee save(Trainee trainee) {
        log.info("Service: saving trainee {}", trainee);
        return traineeDao.save(trainee);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        log.info("Service: fetching trainee with id {}", id);
        return traineeDao.findById(id);
    }

    @Override
    public List<Trainee> findAll() {
        log.info("Service: fetching all trainees");
        return traineeDao.findAll();
    }

    @Override
    public void deleteById(Long id) {
        log.info("Service: deleting trainee with id {}", id);
        traineeDao.deleteById(id);
    }
}
