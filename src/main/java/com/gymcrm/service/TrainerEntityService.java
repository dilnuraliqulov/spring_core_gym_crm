package com.gymcrm.service;

import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface TrainerEntityService {


    Trainer createProfile(String firstName, String lastName, Long specializationId);

    Optional<Trainer> findByUsername(String username);

    Trainer updateProfile(String username, String firstName, String lastName, Long specializationId, boolean isActive);


    List<Training> getTrainings(String username, Date fromDate, Date toDate, String traineeName);

    List<Trainer> findAll();

    Optional<Trainer> findById(Long id);
}
