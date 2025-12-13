package com.gymcrm.service.impl;

import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.entity.User;
import com.gymcrm.exception.InvalidOperationException;
import com.gymcrm.exception.TrainerNotFoundException;
import com.gymcrm.exception.TrainingTypeNotFoundException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.TrainingTypeRepository;
import com.gymcrm.repository.UserRepository;
import com.gymcrm.service.ActivationService;
import com.gymcrm.service.AuthenticationService;
import com.gymcrm.service.TrainerEntityService;
import com.gymcrm.service.UserService;
import com.gymcrm.util.UsernamePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service("trainerServiceEntity")
@RequiredArgsConstructor
@Transactional
public class TrainerEntityServiceImpl implements TrainerEntityService, ActivationService, AuthenticationService {

    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    @Override
    public Trainer createProfile(String firstName, String lastName, Long specializationId) {
        log.info("Creating trainer profile for: {} {}", firstName, lastName);

        validateRequiredFields(firstName, lastName);

        TrainingType specialization = null;
        if (specializationId != null) {
            specialization = trainingTypeRepository.findById(specializationId)
                    .orElseThrow(() -> new TrainingTypeNotFoundException("Training type not found with id: " + specializationId));
        }

        Set<String> existingUsernames = userRepository.findAll().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        String username = UsernamePasswordGenerator.generateUsername(firstName, lastName, existingUsernames);
        log.debug("Generated username: {}", username);

        char[] rawPassword = UsernamePasswordGenerator.generatePassword(10);
        char[] hashedPassword = userService.hashPassword(rawPassword);
        log.debug("Generated password for trainer: {}", username);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);

        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Trainer profile created successfully with username: {}", username);

        // Return raw password for user (will be displayed once)
        savedTrainer.getUser().setPassword(rawPassword);

        return savedTrainer;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findByUsername(String username) {
        log.debug("Finding trainer by username: {}", username);
        return trainerRepository.findByUserUsername(username);
    }

    @Override
    public Trainer updateProfile(String username, String firstName, String lastName,
                                  Long specializationId, boolean isActive) {
        log.info("Updating trainer profile: {}", username);

        validateRequiredFields(firstName, lastName);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + username));

        TrainingType specialization = null;
        if (specializationId != null) {
            specialization = trainingTypeRepository.findById(specializationId)
                    .orElseThrow(() -> new TrainingTypeNotFoundException("Training type not found with id: " + specializationId));
        }

        User user = trainer.getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(isActive);

        trainer.setSpecialization(specialization);

        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Trainer profile updated successfully: {}", username);

        return updatedTrainer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, Date fromDate, Date toDate, String traineeName) {
        log.info("Getting trainings for trainer: {} with criteria", username);

        if (!trainerRepository.existsByUserUsername(username)) {
            throw new TrainerNotFoundException("Trainer not found: " + username);
        }

        List<Training> trainings = trainingRepository.findTrainerTrainingsByCriteria(
                username, fromDate, toDate, traineeName);

        log.debug("Found {} trainings for trainer: {}", trainings.size(), username);
        return trainings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> findAll() {
        log.debug("Finding all trainers");
        return trainerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> findById(Long id) {
        log.debug("Finding trainer by id: {}", id);
        return trainerRepository.findById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, char[] password) {
        log.debug("Authenticating trainer: {}", username);

        // Verify this is a trainer
        if (!trainerRepository.existsByUserUsername(username)) {
            throw new TrainerNotFoundException("Trainer not found: " + username);
        }

        // Delegate to UserService
        return userService.authenticate(username, password);
    }

    @Override
    public void changePassword(String username, char[] currentPassword, char[] newPassword) {
        log.info("Changing password for trainer: {}", username);

        // Verify this is a trainer
        if (!trainerRepository.existsByUserUsername(username)) {
            throw new TrainerNotFoundException("Trainer not found: " + username);
        }

        // Delegate to UserService
        userService.changePassword(username, currentPassword, newPassword);
        log.info("Password changed successfully for trainer: {}", username);
    }

    @Override
    public void activate(String username) {
        log.info("Activating trainer: {}", username);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + username));

        User user = trainer.getUser();

        // Non-idempotent check
        if (user.isActive()) {
            log.warn("Trainer is already active: {}", username);
            throw new InvalidOperationException("Trainer is already active: " + username);
        }

        user.setActive(true);
        userService.save(user);
        log.info("Trainer activated successfully: {}", username);
    }

    @Override
    public void deactivate(String username) {
        log.info("Deactivating trainer: {}", username);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + username));

        User user = trainer.getUser();

        // Non-idempotent check
        if (!user.isActive()) {
            log.warn("Trainer is already inactive: {}", username);
            throw new InvalidOperationException("Trainer is already inactive: " + username);
        }

        user.setActive(false);
        userService.save(user);
        log.info("Trainer deactivated successfully: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isActive(String username) {
        log.debug("Checking if trainer is active: {}", username);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + username));

        return trainer.getUser().isActive();
    }

    private void validateRequiredFields(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new ValidationException("Last name is required");
        }
    }
}
