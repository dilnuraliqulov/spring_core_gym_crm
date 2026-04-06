package com.gymcrm.bdd.common.stepdefs;

import com.gymcrm.repository.TraineeRepository;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.TrainingTypeRepository;
import com.gymcrm.repository.UserRepository;
import io.cucumber.java.en.Given;

public class CommonDatabaseSteps {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;

    public CommonDatabaseSteps(
            TrainingRepository trainingRepository,
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            TrainingTypeRepository trainingTypeRepository,
            UserRepository userRepository
    ) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.userRepository = userRepository;
    }

    @Given("the database is clean")
    public void the_database_is_clean() {
        trainingRepository.deleteAll();
        traineeRepository.deleteAll();
        trainerRepository.deleteAll();
        trainingTypeRepository.deleteAll();
        userRepository.deleteAll();
    }
}