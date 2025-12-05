package com.gymcrm.service.impl.core;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final GenericDao<Trainer> trainerDao;

    @Override
    public Trainer save (Trainer trainer) {
        log.debug("Service:saving trainer {}", trainer);
        return trainerDao.save(trainer);
    }
    @Override
    public Optional<Trainer> findById(Long id) {
        log.info("Service: fetching trainer with id {}", id);
        return trainerDao.findById(id);
    }

    @Override
    public List<Trainer> findAll() {
        log.info("Service: fetching all trainers");
        return trainerDao.findAll();
    }

}
