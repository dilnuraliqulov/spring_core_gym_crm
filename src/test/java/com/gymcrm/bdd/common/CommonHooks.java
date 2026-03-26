package com.gymcrm.bdd.common;

import com.gymcrm.repository.TraineeRepository;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.TrainingTypeRepository;
import com.gymcrm.repository.UserRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class CommonHooks {

    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final ScenarioContext scenarioContext;

    public CommonHooks(
            UserRepository userRepository,
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            TrainingRepository trainingRepository,
            TrainingTypeRepository trainingTypeRepository,
            ScenarioContext scenarioContext
    ) {
        this.userRepository = userRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.scenarioContext = scenarioContext;
    }

    @Before(order = 100)
    public void beforeScenario() {
        cleanDatabase();
        scenarioContext.clear();
    }

    @After(order = 100)
    public void afterScenario() {
        scenarioContext.clear();
    }

    private void cleanDatabase() {
        trainingRepository.deleteAll();
        traineeRepository.deleteAll();
        trainerRepository.deleteAll();
        trainingTypeRepository.deleteAll();
        userRepository.deleteAll();
    }
}