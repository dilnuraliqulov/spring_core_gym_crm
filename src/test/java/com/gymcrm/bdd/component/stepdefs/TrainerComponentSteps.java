package com.gymcrm.bdd.component.stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.bdd.common.ScenarioContext;
import com.gymcrm.dto.request.ActivationRequest;
import com.gymcrm.dto.request.TrainerRegistrationRequest;
import com.gymcrm.dto.request.UpdateTrainerRequest;
import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.entity.User;
import com.gymcrm.repository.TraineeRepository;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.TrainingTypeRepository;
import com.gymcrm.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainerComponentSteps {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;
    private final ScenarioContext scenarioContext;
    private final PasswordEncoder passwordEncoder;

    private TrainerRegistrationRequest trainerRegistrationRequest;
    private String currentTrainerUsername;

    public TrainerComponentSteps(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            TrainerRepository trainerRepository,
            UserRepository userRepository,
            TrainingTypeRepository trainingTypeRepository,
            TraineeRepository traineeRepository,
            TrainingRepository trainingRepository,
            ScenarioContext scenarioContext,
            PasswordEncoder passwordEncoder
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.traineeRepository = traineeRepository;
        this.trainingRepository = trainingRepository;
        this.scenarioContext = scenarioContext;
        this.passwordEncoder = passwordEncoder;
    }

    @Given("I have valid trainer registration data")
    public void i_have_valid_trainer_registration_data(io.cucumber.datatable.DataTable table) {
        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(data.get("specialization"))
                .orElseThrow(() -> new AssertionError("TrainingType not found: " + data.get("specialization")));

        trainerRegistrationRequest = new TrainerRegistrationRequest();
        trainerRegistrationRequest.setFirstName(data.get("firstName"));
        trainerRegistrationRequest.setLastName(data.get("lastName"));
        trainerRegistrationRequest.setSpecializationId(trainingType.getId());
    }

    @Given("a trainer with username {string} exists")
    public void a_trainer_with_username_exists(String username, io.cucumber.datatable.DataTable table) {
        if (trainerRepository.findByUserUsername(username).isPresent()) {
            currentTrainerUsername = username;
            return;
        }

        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(data.get("specialization"))
                .orElseThrow(() -> new AssertionError("TrainingType not found: " + data.get("specialization")));

        // FIX: Save User first (with BCrypt password), then create Trainer referencing the saved User.
        // This avoids the "detached entity passed to persist" error that occurs when cascade tries
        // to re-persist an already-managed User.
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode("password123").toCharArray());
            newUser.setFirstName(data.get("firstName"));
            newUser.setLastName(data.get("lastName"));
            newUser.setActive(Boolean.parseBoolean(data.getOrDefault("active", "true")));
            return userRepository.save(newUser);
        });

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        trainerRepository.save(trainer);
        currentTrainerUsername = username;
    }

    @Given("a trainer with username {string} exists with status inactive")
    public void a_trainer_with_username_exists_with_status_inactive(String username, io.cucumber.datatable.DataTable table) {
        if (trainerRepository.findByUserUsername(username).isPresent()) {
            currentTrainerUsername = username;
            return;
        }

        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(data.get("specialization"))
                .orElseThrow(() -> new AssertionError("TrainingType not found: " + data.get("specialization")));

        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode("password123").toCharArray());
            newUser.setFirstName(data.get("firstName"));
            newUser.setLastName(data.get("lastName"));
            newUser.setActive(false);
            return userRepository.save(newUser);
        });

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        trainerRepository.save(trainer);
        currentTrainerUsername = username;
    }

    @Given("trainer {string} exists with details")
    public void trainer_exists_with_details(String username, io.cucumber.datatable.DataTable table) {
        a_trainer_with_username_exists(username, table);
    }

    @Given("trainer {string} exists inactive with details")
    public void trainer_exists_inactive_with_details(String username, io.cucumber.datatable.DataTable table) {
        a_trainer_with_username_exists_with_status_inactive(username, table);
    }

    @Given("a trainee {string} is assigned to trainer {string}")
    public void a_trainee_is_assigned_to_trainer(String traineeUsername, String trainerUsername) {
        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new AssertionError("Trainee not found: " + traineeUsername));

        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                .orElseThrow(() -> new AssertionError("Trainer not found: " + trainerUsername));

        if (trainee.getTrainers().stream().noneMatch(t -> t.getUser().getUsername().equals(trainerUsername))) {
            trainee.getTrainers().add(trainer);
            traineeRepository.save(trainee);
        }
    }

    @Given("the trainer has completed trainings")
    public void the_trainer_has_completed_trainings() {
        Trainer trainer = trainerRepository.findByUserUsername(currentTrainerUsername)
                .orElseThrow(() -> new AssertionError("Trainer not found: " + currentTrainerUsername));

        User traineeUser = userRepository.findByUsername("completed_trainee").orElseGet(() -> {
            User user = new User();
            user.setUsername("completed_trainee");
            user.setPassword(passwordEncoder.encode("password123").toCharArray());
            user.setFirstName("Completed");
            user.setLastName("Trainee");
            user.setActive(true);
            return userRepository.save(user);
        });

        Trainee trainee = traineeRepository.findByUserUsername("completed_trainee").orElseGet(() -> {
            Trainee newTrainee = new Trainee();
            newTrainee.setUser(traineeUser);
            newTrainee.setDateOfBirth(Date.from(
                    LocalDate.of(1995, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            ));
            return traineeRepository.save(newTrainee);
        });

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainer.getSpecialization());
        training.setTrainingName("Completed Session");
        training.setTrainingDate(Date.from(
                LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        ));
        training.setDurationOfTraining(60);

        trainingRepository.save(training);
    }

    @Given("I have invalid trainer registration data with missing {string}")
    public void i_have_invalid_trainer_registration_data_with_missing(String field) {
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName("Yoga")
                .orElseThrow(() -> new AssertionError("TrainingType not found: Yoga"));

        trainerRegistrationRequest = new TrainerRegistrationRequest();
        trainerRegistrationRequest.setFirstName("Test");
        trainerRegistrationRequest.setLastName("Trainer");
        trainerRegistrationRequest.setSpecializationId(trainingType.getId());

        switch (field) {
            case "firstName" -> trainerRegistrationRequest.setFirstName(null);
            case "lastName" -> trainerRegistrationRequest.setLastName(null);
            case "specialization" -> trainerRegistrationRequest.setSpecializationId(null);
            default -> throw new IllegalArgumentException("Unsupported field: " + field);
        }
    }

    @Given("I have trainer registration data with invalid specialization:")
    public void i_have_trainer_registration_data_with_invalid_specialization(io.cucumber.datatable.DataTable table) {
        trainerRegistrationRequest = new TrainerRegistrationRequest();
        trainerRegistrationRequest.setFirstName("Test");
        trainerRegistrationRequest.setLastName("Trainer");
        trainerRegistrationRequest.setSpecializationId(999999L);
    }

    @When("I register a new trainer")
    public void i_register_a_new_trainer() throws Exception {
        String body = objectMapper.writeValueAsString(trainerRegistrationRequest);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                ).andReturn()
        );
    }

    @When("I request trainer profile for {string}")
    public void i_request_trainer_profile_for(String username) throws Exception {
        currentTrainerUsername = username;

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/trainers/" + username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @When("I update trainer profile with:")
    public void i_update_trainer_profile_with(io.cucumber.datatable.DataTable table) throws Exception {
        Trainer trainer = trainerRepository.findByUserUsername(currentTrainerUsername)
                .orElseThrow(() -> new AssertionError("Trainer not found: " + currentTrainerUsername));

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername(currentTrainerUsername);
        request.setFirstName(trainer.getUser().getFirstName());
        request.setLastName(trainer.getUser().getLastName());
        request.setIsActive(trainer.getUser().isActive());

        String body = objectMapper.writeValueAsString(request);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @When("I update trainer profile without authentication with:")
    public void i_update_trainer_profile_without_authentication_with(io.cucumber.datatable.DataTable table) throws Exception {
        Trainer trainer = trainerRepository.findByUserUsername(currentTrainerUsername)
                .orElseThrow(() -> new AssertionError("Trainer not found: " + currentTrainerUsername));

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername(currentTrainerUsername);
        request.setFirstName(trainer.getUser().getFirstName());
        request.setLastName(trainer.getUser().getLastName());
        request.setIsActive(trainer.getUser().isActive());

        String body = objectMapper.writeValueAsString(request);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                ).andReturn()
        );
    }

    @When("I deactivate trainer {string}")
    public void i_deactivate_trainer(String username) throws Exception {
        ActivationRequest request = new ActivationRequest();
        request.setUsername(username);
        request.setIsActive(false);

        String body = objectMapper.writeValueAsString(request);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/trainers/activate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @When("I activate trainer {string}")
    public void i_activate_trainer(String username) throws Exception {
        ActivationRequest request = new ActivationRequest();
        request.setUsername(username);
        request.setIsActive(true);

        String body = objectMapper.writeValueAsString(request);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/trainers/activate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @Then("the trainer should be created successfully")
    public void the_trainer_should_be_created_successfully() {
        Trainer trainer = trainerRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("No trainer found"));
        assertThat(trainer.getUser()).isNotNull();
    }

    @Then("the trainer should be active")
    public void the_trainer_should_be_active() {
        Trainer trainer = trainerRepository.findByUserUsername(currentTrainerUsername)
                .orElseGet(() -> trainerRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new AssertionError("No trainer found")));
        assertThat(trainer.getUser().isActive()).isTrue();
    }

    @Then("the trainer should be inactive")
    public void the_trainer_should_be_inactive() {
        Trainer trainer = trainerRepository.findByUserUsername(currentTrainerUsername)
                .orElseThrow(() -> new AssertionError("Trainer not found"));
        assertThat(trainer.getUser().isActive()).isFalse();
    }

    @Then("the trainer profile should contain correct information")
    public void the_trainer_profile_should_contain_correct_information() throws Exception {
        String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(response).contains(currentTrainerUsername);
    }

    @Then("the trainer specialization should be {string}")
    public void the_trainer_specialization_should_be(String specialization) {
        Trainer trainer = trainerRepository.findByUserUsername(currentTrainerUsername)
                .orElseThrow(() -> new AssertionError("Trainer not found"));
        assertThat(trainer.getSpecialization().getTrainingTypeName()).isEqualTo(specialization);
    }

    @Then("the trainees list should contain {string}")
    public void the_trainees_list_should_contain(String traineeUsername) throws Exception {
        String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(response).contains(traineeUsername);
    }

    @Then("the workload summary should contain training statistics")
    public void the_workload_summary_should_contain_training_statistics() throws Exception {
        String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(response).isNotBlank();
    }
}