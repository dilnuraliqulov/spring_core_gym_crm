package com.gymcrm.service.impl;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.User;
import com.gymcrm.exception.InvalidOperationException;
import com.gymcrm.exception.TraineeNotFoundException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.repository.TraineeRepository;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.UserRepository;
import com.gymcrm.service.ActivationService;
import com.gymcrm.service.AuthenticationService;
import com.gymcrm.service.TraineeEntityService;
import com.gymcrm.service.UserService;
import com.gymcrm.util.UsernamePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("traineeServiceEntity")
@RequiredArgsConstructor
@Transactional
public class TraineeEntityServiceImpl implements TraineeEntityService, ActivationService, AuthenticationService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    @Override
    public Trainee createProfile(String firstName, String lastName, Date dateOfBirth, String address) {
        log.info("Creating trainee profile for: {} {}", firstName, lastName);

        validateRequiredFields(firstName, lastName);

        Set<String> existingUsernames = userRepository.findAll().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        String username = UsernamePasswordGenerator.generateUsername(firstName, lastName, existingUsernames);
        log.debug("Generated username: {}", username);

        char[] rawPassword = UsernamePasswordGenerator.generatePassword(10);
        char[] hashedPassword = userService.hashPassword(rawPassword);
        log.debug("Generated password for trainee: {}", username);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Trainee profile created successfully with username: {}", username);

        // Return raw password for user (will be displayed once)
        savedTrainee.getUser().setPassword(rawPassword);

        return savedTrainee;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> findByUsername(String username) {
        log.debug("Finding trainee by username: {}", username);
        return traineeRepository.findByUserUsername(username);
    }

    @Override
    public Trainee updateProfile(String username, String firstName, String lastName,
                                 Date dateOfBirth, String address, boolean isActive) {
        log.info("Updating trainee profile: {}", username);

        validateRequiredFields(firstName, lastName);

              Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + username));

        User user = trainee.getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(isActive);

        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Trainee profile updated successfully: {}", username);

        return updatedTrainee;
    }

    @Override
    public void deleteByUsername(String username) {
        log.info("Deleting trainee by username: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + username));

        // Hard delete - cascade will delete related trainings
        traineeRepository.delete(trainee);

        log.info("Trainee deleted successfully: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, Date fromDate, Date toDate,
                                        String trainerName, String trainingTypeName) {
        log.info("Getting trainings for trainee: {} with criteria", username);

        if (!traineeRepository.existsByUserUsername(username)) {
            throw new TraineeNotFoundException("Trainee not found: " + username);
        }

        List<Training> trainings = trainingRepository.findTraineeTrainingsByCriteria(
                username, fromDate, toDate, trainerName, trainingTypeName);

        log.debug("Found {} trainings for trainee: {}", trainings.size(), username);
        return trainings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String username) {
        log.info("Getting unassigned trainers for trainee: {}", username);

        if (!traineeRepository.existsByUserUsername(username)) {
            throw new TraineeNotFoundException("Trainee not found: " + username);
        }

        List<Trainer> trainers = trainerRepository.findTrainersNotAssignedToTrainee(username);
        log.debug("Found {} unassigned trainers for trainee: {}", trainers.size(), username);

        return trainers;
    }

    @Override
    public Trainee updateTrainersList(String username, List<String> trainerUsernames) {
        log.info("Updating trainers list for trainee: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + username));

        List<Trainer> trainers = trainerRepository.findByUserUsernameIn(trainerUsernames);

        if (trainers.size() != trainerUsernames.size()) {
            Set<String> foundUsernames = trainers.stream()
                    .map(t -> t.getUser().getUsername())
                    .collect(Collectors.toSet());
            List<String> notFound = trainerUsernames.stream()
                    .filter(u -> !foundUsernames.contains(u))
                    .collect(Collectors.toList());
            log.warn("Some trainers not found: {}", notFound);
        }

        trainee.setTrainers(new ArrayList<>(trainers));
       Trainee updatedTrainee = traineeRepository.save(trainee);

        log.info("Trainers list updated successfully for trainee: {}", username);
        return updatedTrainee;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> findAll() {
        log.debug("Finding all trainees");
        return traineeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> findById(Long id) {
        log.debug("Finding trainee by id: {}", id);
        return traineeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, char[] password) {
        log.debug("Authenticating trainee: {}", username);

        // Verify this is a trainee
        if (!traineeRepository.existsByUserUsername(username)) {
            throw new TraineeNotFoundException("Trainee not found: " + username);
        }

        // Delegate to UserService
        return userService.authenticate(username, password);
    }

    @Override
    public void changePassword(String username, char[] currentPassword, char[] newPassword) {
        log.info("Changing password for trainee: {}", username);

        // Verify this is a trainee
        if (!traineeRepository.existsByUserUsername(username)) {
            throw new TraineeNotFoundException("Trainee not found: " + username);
        }

        // Delegate to UserService
        userService.changePassword(username, currentPassword, newPassword);
        log.info("Password changed successfully for trainee: {}", username);
    }


    @Override
    public void activate(String username) {
        log.info("Activating trainee: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + username));

        User user = trainee.getUser();

        if (user.isActive()) {
            log.warn("Trainee is already active: {}", username);
            throw new InvalidOperationException("Trainee is already active: " + username);
        }

        user.setActive(true);
        userService.save(user);
        log.info("Trainee activated successfully: {}", username);
    }

    @Override
    public void deactivate(String username) {
        log.info("Deactivating trainee: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + username));

        User user = trainee.getUser();

        // Non-idempotent check
        if (!user.isActive()) {
            log.warn("Trainee is already inactive: {}", username);
            throw new InvalidOperationException("Trainee is already inactive: " + username);
        }

        user.setActive(false);
        userService.save(user);
        log.info("Trainee deactivated successfully: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isActive(String username) {
        log.debug("Checking if trainee is active: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found: " + username));

        return trainee.getUser().isActive();
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

