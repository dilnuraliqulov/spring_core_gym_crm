package com.gymcrm.service.impl;

import com.gymcrm.dao.GenericDao;
import com.gymcrm.model.Trainee;
import com.gymcrm.service.TraineeService;
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
public class TraineeServiceImpl implements TraineeService {

    private final GenericDao<Trainee> traineeDao;

    @Override
    public Trainee save(Trainee trainee) {
        // Collect existing usernames
        Set<String> existingUsernames = traineeDao.findAll().stream()
                .map(Trainee::getUsername)
                .collect(Collectors.toSet());

        // Generate username if missing
        if (trainee.getUsername() == null || trainee.getUsername().isBlank()) {
            String generatedUsername = UsernamePasswordGenerator.generateUsername(
                    trainee.getFirstName(),
                    trainee.getLastName(),
                    existingUsernames
            );
            trainee.setUsername(generatedUsername);
            log.debug("Generated username for trainee {} {}: {}",
                    trainee.getFirstName(), trainee.getLastName(), generatedUsername);
        }

        // Generate password if missing
        if (trainee.getPassword() == null || trainee.getPassword().isBlank()) {
            String generatedPassword = UsernamePasswordGenerator.generatePassword();
            trainee.setPassword(generatedPassword);
            log.debug("Generated password for trainee {}: {}", trainee.getUsername(), generatedPassword);
        }

        log.debug("Saving trainee: {}", trainee);
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
