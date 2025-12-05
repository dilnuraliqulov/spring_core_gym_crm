package com.gymcrm.service.impl.core;

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
        log.debug("Service: creating training {}", training);

        if (training.getId() != null && trainingDao.findById(training.getId()).isPresent()) {
            throw new UnsupportedOperationException(
                    "Updating existing training is not supported. Training id: " + training.getId()
            );
        }

        return trainingDao.save(training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.debug("Service: fetching training with id {}", id);
        return trainingDao.findById(id);
    }

    @Override
    public List<Training> findAll() {
        log.debug("Service: fetching all trainings");
        return trainingDao.findAll();
    }


}
