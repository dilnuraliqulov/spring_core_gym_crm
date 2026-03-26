package com.gymcrm.bdd.common;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.entity.User;

import java.time.LocalDate;
import java.util.Date;

/**
 * Builder for creating test data entities with fluent API
 */
public class TestDataBuilder {

    public UserBuilder user() {
        return new UserBuilder();
    }

    public TraineeBuilder trainee() {
        return new TraineeBuilder();
    }

    public TrainerBuilder trainer() {
        return new TrainerBuilder();
    }

    public TrainingBuilder training() {
        return new TrainingBuilder();
    }

    public TrainingTypeBuilder trainingType() {
        return new TrainingTypeBuilder();
    }

    public static class UserBuilder {
        private String username;
        private String password = "password";
        private String firstName = "Test";
        private String lastName = "User";
        private boolean active = true;

        public UserBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder inactive() {
            this.active = false;
            return this;
        }

        public User build() {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password.toCharArray());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setActive(active);
            return user;
        }
    }

    public static class TraineeBuilder {
        private User user;
        private Date dateOfBirth = Date.from(LocalDate.of(1990, 1, 1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());

        public TraineeBuilder withUser(User user) {
            this.user = user;
            return this;
        }

        public TraineeBuilder withDateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Trainee build() {
            Trainee trainee = new Trainee();
            trainee.setUser(user);
            trainee.setDateOfBirth(dateOfBirth);
            return trainee;
        }
    }

    public static class TrainerBuilder {
        private User user;
        private TrainingType specialization;

        public TrainerBuilder withUser(User user) {
            this.user = user;
            return this;
        }

        public TrainerBuilder withSpecialization(TrainingType specialization) {
            this.specialization = specialization;
            return this;
        }

        public Trainer build() {
            Trainer trainer = new Trainer();
            trainer.setUser(user);
            trainer.setSpecialization(specialization);
            return trainer;
        }
    }

    public static class TrainingBuilder {
        private Trainee trainee;
        private Trainer trainer;
        private String trainingName = "Test Training";
        private Date trainingDate = new Date();
        private int durationOfTraining = 60;
        private TrainingType trainingType;

        public TrainingBuilder withTrainee(Trainee trainee) {
            this.trainee = trainee;
            return this;
        }

        public TrainingBuilder withTrainer(Trainer trainer) {
            this.trainer = trainer;
            return this;
        }

        public TrainingBuilder withTrainingName(String trainingName) {
            this.trainingName = trainingName;
            return this;
        }

        public TrainingBuilder withTrainingDate(Date trainingDate) {
            this.trainingDate = trainingDate;
            return this;
        }

        public TrainingBuilder withDuration(int durationOfTraining) {
            this.durationOfTraining = durationOfTraining;
            return this;
        }

        public TrainingBuilder withTrainingType(TrainingType trainingType) {
            this.trainingType = trainingType;
            return this;
        }

        public Training build() {
            Training training = new Training();
            training.setTrainee(trainee);
            training.setTrainer(trainer);
            training.setTrainingName(trainingName);
            training.setTrainingDate(trainingDate);
            training.setDurationOfTraining(durationOfTraining);
            training.setTrainingType(trainingType);
            return training;
        }
    }

    public static class TrainingTypeBuilder {
        private String trainingTypeName = "Default";

        public TrainingTypeBuilder withName(String trainingTypeName) {
            this.trainingTypeName = trainingTypeName;
            return this;
        }

        public TrainingType build() {
            TrainingType type = new TrainingType();
            type.setTrainingTypeName(trainingTypeName);
            return type;
        }
    }
}

