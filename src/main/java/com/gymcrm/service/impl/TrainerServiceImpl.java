package com.gymcrm.service.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TrainerService;
import com.gymcrm.util.UsernamePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final GenericDao<Trainer> trainerDao;

    @Override
    public Trainer save(Trainer trainer) {
        // Collect existing usernames
        Set<String> existingUsernames = trainerDao.findAll().stream()
                .map(Trainer::getUsername)
                .collect(Collectors.toSet());

        // Generate username if missing
        if (trainer.getUsername() == null || trainer.getUsername().isBlank()) {
            String generatedUsername = UsernamePasswordGenerator.generateUsername(
                    trainer.getFirstName(),
                    trainer.getLastName(),
                    existingUsernames
            );
            trainer.setUsername(generatedUsername);
            log.debug("Generated username for trainer {} {}: {}",
                    trainer.getFirstName(), trainer.getLastName(), generatedUsername);
        }

        // Generate password if missing
        if (trainer.getPassword() == null || trainer.getPassword().isBlank()) {
            String generatedPassword = UsernamePasswordGenerator.generatePassword();
            trainer.setPassword(generatedPassword);
            log.debug("Generated password for trainer {}: {}", trainer.getUsername(), generatedPassword);
        }

        log.debug("Saving trainer: {}", trainer);
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
