package com.gymcrm.service;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface TraineeEntityService {


    Trainee createProfile(String firstName, String lastName, Date dateOfBirth, String address);


    Optional<Trainee> findByUsername(String username);


    Trainee updateProfile(String username, String firstName, String lastName, Date dateOfBirth, String address, boolean isActive);

    void deleteByUsername(String username);

    List<Training> getTrainings(String username, Date fromDate, Date toDate, String trainerName, String trainingTypeName);


    List<Trainer> getUnassignedTrainers(String username);

    Trainee updateTrainersList(String username, List<String> trainerUsernames);

    List<Trainee> findAll();

    Optional<Trainee> findById(Long id);
}
