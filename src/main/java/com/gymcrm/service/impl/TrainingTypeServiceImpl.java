package com.gymcrm.service.impl;

import com.gymcrm.entity.TrainingType;
import com.gymcrm.repository.TrainingTypeRepository;
import com.gymcrm.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public List<TrainingType> findAll() {
        log.debug("Finding all training types");
        return trainingTypeRepository.findAll();
    }

    @Override
    public Optional<TrainingType> findById(Long id) {
        log.debug("Finding training type by id: {}", id);
        return trainingTypeRepository.findById(id);
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        log.debug("Finding training type by name: {}", name);
        return trainingTypeRepository.findByTrainingTypeName(name);
    }
}

