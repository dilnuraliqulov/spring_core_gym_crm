package com.gymcrm;

import com.gymcrm.config.AppConfig;
import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.facade.GymEntityFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Load Spring context
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        log.info("Spring Context Loaded Successfully!");

        GymEntityFacade entityFacade = context.getBean(GymEntityFacade.class);

        // 1. Get all training types (no auth required - read-only reference data)
        log.info("Getting All Training Types");
        List<TrainingType> trainingTypes = entityFacade.getAllTrainingTypes();
        trainingTypes.forEach(tt -> log.info("Training Type: id={}, name={}", tt.getId(), tt.getTrainingTypeName()));

        if (trainingTypes.isEmpty()) {
            log.error("No training types found! Please check database migration.");
            return;
        }

        //2. Create Trainee Profile (NO AUTH REQUIRED)
        log.info("Creating Trainee Profile ");
        Trainee trainee = entityFacade.createTraineeProfile("John", "Doe", new Date(), "123 Main Street");
        String traineeUsername = trainee.getUser().getUsername();
        // Store raw password as String to avoid char[] mutation issues
        String traineePasswordStr = new String(trainee.getUser().getPassword());
        log.info("Trainee created: username={}, password={}", traineeUsername, traineePasswordStr);

        //3. Create Trainer Profile (NO AUTH REQUIRED)
        log.info("Creating Trainer Profile ");
        Long trainingTypeId = trainingTypes.get(0).getId();
        Trainer trainer = entityFacade.createTrainerProfile("Jane", "Smith", trainingTypeId);
        String trainerUsername = trainer.getUser().getUsername();
        String trainerPasswordStr = new String(trainer.getUser().getPassword());
        log.info("Trainer created: username={}, password={}", trainerUsername, trainerPasswordStr);

        // 4. Authenticate Trainee (validates credentials)
        log.info(" Authenticating Trainee");
        boolean traineeAuth = entityFacade.authenticateTrainee(traineeUsername, traineePasswordStr.toCharArray());
        log.info("Trainee authentication result: {}", traineeAuth);

        //5. Authenticate Trainer (validates credentials)
        log.info(" Authenticating Trainer");
        boolean trainerAuth = entityFacade.authenticateTrainer(trainerUsername, trainerPasswordStr.toCharArray());
        log.info("Trainer authentication result: {}", trainerAuth);

        // 6. Get Trainee by Username (REQUIRES AUTH)
        log.info("Getting Trainee Profile (requires auth)");
        Optional<Trainee> foundTrainee = entityFacade.getTraineeByUsername(traineeUsername, traineePasswordStr.toCharArray());
        foundTrainee.ifPresent(t -> log.info("Found trainee: {} {}",
                t.getUser().getFirstName(), t.getUser().getLastName()));

        // 7. Get Trainer by Username (REQUIRES AUTH)
        log.info("Getting Trainer Profile (requires auth)");
        Optional<Trainer> foundTrainer = entityFacade.getTrainerByUsername(trainerUsername, trainerPasswordStr.toCharArray());
        foundTrainer.ifPresent(t -> log.info("Found trainer: {} {}, specialization={}",
                t.getUser().getFirstName(), t.getUser().getLastName(),
                t.getSpecialization() != null ? t.getSpecialization().getTrainingTypeName() : "N/A"));

        //  8. Update Trainee Profile (REQUIRES AUTH)
        log.info(" Updating Trainee Profile (requires auth");
        Trainee updatedTrainee = entityFacade.updateTraineeProfile(traineeUsername, traineePasswordStr.toCharArray(),
                "John", "Doe-Updated", new Date(), "456 New Address", true);
        log.info("Trainee updated: lastName={}", updatedTrainee.getUser().getLastName());

        // 9. Update Trainer Profile (REQUIRES AUTH)
        log.info("Updating Trainer Profile (requires auth) ");
        Trainer updatedTrainer = entityFacade.updateTrainerProfile(trainerUsername, trainerPasswordStr.toCharArray(),
                "Jane", "Smith-Updated", trainingTypeId, true);
        log.info("Trainer updated: lastName={}", updatedTrainer.getUser().getLastName());

    }
}
