package com.gymcrm.service.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Training;
import com.gymcrm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final GenericDao<Training> trainingDao;

    @Override
    public Training save(Training training) {
        log.info("Service: saving training {}", training);
        return trainingDao.save(training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.info("Service: fetching training with id {}", id);
        return trainingDao.findById(id);
    }

    @Override
    public List<Training> findAll() {
        log.info("Service: fetching all trainings");
        return trainingDao.findAll();
    }

    @Override
    public void deleteById(Long id) {
        log.info("Service: deleting training with id {}", id);
        trainingDao.deleteById(id);
    }
}
