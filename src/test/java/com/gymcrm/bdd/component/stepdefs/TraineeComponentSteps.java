package com.gymcrm.bdd.component.stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.bdd.common.ScenarioContext;
import com.gymcrm.dto.request.ActivationRequest;
import com.gymcrm.dto.request.UpdateTraineeRequest;
import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.User;
import com.gymcrm.exception.ResourceNotFoundException;
import com.gymcrm.repository.TraineeRepository;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.UserRepository;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TraineeComponentSteps {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final ScenarioContext scenarioContext;
    // FIX: Inject PasswordEncoder so test-created users have BCrypt-encoded passwords
    private final PasswordEncoder passwordEncoder;

    private final Map<String, Object> traineeRegistrationData = new HashMap<>();
    private String currentTraineeUsername;

    public TraineeComponentSteps(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            TraineeRepository traineeRepository,
            UserRepository userRepository,
            TrainerRepository trainerRepository,
            ScenarioContext scenarioContext,
            PasswordEncoder passwordEncoder
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.scenarioContext = scenarioContext;
        this.passwordEncoder = passwordEncoder;
    }

    @DataTableType
    public Map<String, String> mapTraineeData(Map<String, String> entry) {
        return entry;
    }

    @Given("I have valid trainee registration data")
    public void i_have_valid_trainee_registration_data(io.cucumber.datatable.DataTable table) {
        Map<String, String> data = table.asMaps(String.class, String.class).get(0);
        traineeRegistrationData.clear();
        traineeRegistrationData.put("firstName", data.get("firstName"));
        traineeRegistrationData.put("lastName", data.get("lastName"));
        traineeRegistrationData.put("dateOfBirth", data.get("dateOfBirth"));
    }

    @Given("a trainee with username {string} exists")
    public void a_trainee_with_username_exists(String username, io.cucumber.datatable.DataTable table) {
        if (traineeRepository.findByUserUsername(username).isPresent()) {
            currentTraineeUsername = username;
            return;
        }

        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        // FIX: encode password with BCrypt so auth header X-Password: password123 matches
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode("password123").toCharArray());
            newUser.setFirstName(data.get("firstName"));
            newUser.setLastName(data.get("lastName"));
            newUser.setActive(Boolean.parseBoolean(data.getOrDefault("active", "true")));
            return userRepository.save(newUser);
        });

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(Date.from(
                LocalDate.parse(data.get("dateOfBirth"), DateTimeFormatter.ISO_DATE)
                        .atStartOfDay(java.time.ZoneId.systemDefault())
                        .toInstant()
        ));

        traineeRepository.save(trainee);
        currentTraineeUsername = username;
    }

    @Given("the trainee {string} is assigned to trainer {string}")
    public void the_trainee_is_assigned_to_trainer(String traineeUsername, String trainerUsername) {
        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        if (trainee.getTrainers().stream().noneMatch(t -> t.getUser().getUsername().equals(trainerUsername))) {
            trainee.getTrainers().add(trainer);
            traineeRepository.save(trainee);
        }
    }

    @Given("I have invalid trainee registration data with missing {string}")
    public void i_have_invalid_trainee_registration_data_with_missing(String field) {
        traineeRegistrationData.clear();
        traineeRegistrationData.put("firstName", "TestName");
        traineeRegistrationData.put("lastName", "TestLast");
        traineeRegistrationData.put("dateOfBirth", "1990-01-01");
        traineeRegistrationData.remove(field);
    }

    @Given("I have trainee registration data with invalid date:")
    public void i_have_trainee_registration_data_with_invalid_date(io.cucumber.datatable.DataTable table) {
        Map<String, String> data = table.asMaps(String.class, String.class).get(0);
        traineeRegistrationData.clear();
        traineeRegistrationData.put("firstName", "TestName");
        traineeRegistrationData.put("lastName", "TestLast");
        traineeRegistrationData.put("dateOfBirth", data.get("dateOfBirth"));
    }

    @When("I register a new trainee")
    public void i_register_a_new_trainee() throws Exception {
        String body = objectMapper.writeValueAsString(traineeRegistrationData);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                ).andReturn()
        );
    }

    @When("I request trainee profile for {string}")
    public void i_request_trainee_profile_for(String username) throws Exception {
        currentTraineeUsername = username;

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/trainees/" + username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @When("I update trainee profile with:")
    public void i_update_trainee_profile_with(io.cucumber.datatable.DataTable table) throws Exception {
        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        Trainee trainee = traineeRepository.findByUserUsername(currentTraineeUsername)
                .orElseThrow(() -> new AssertionError("Trainee not found: " + currentTraineeUsername));

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername(currentTraineeUsername);
        request.setFirstName(data.getOrDefault("firstName", trainee.getUser().getFirstName()));
        request.setLastName(data.getOrDefault("lastName", trainee.getUser().getLastName()));
        request.setDateOfBirth(trainee.getDateOfBirth());
        request.setAddress(trainee.getAddress());
        request.setIsActive(trainee.getUser().isActive());

        String body = objectMapper.writeValueAsString(request);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @When("I update trainee profile without authentication with:")
    public void i_update_trainee_profile_without_authentication_with(io.cucumber.datatable.DataTable table) throws Exception {
        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        Trainee trainee = traineeRepository.findByUserUsername(currentTraineeUsername)
                .orElseThrow(() -> new AssertionError("Trainee not found: " + currentTraineeUsername));

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername(currentTraineeUsername);
        request.setFirstName(data.getOrDefault("firstName", trainee.getUser().getFirstName()));
        request.setLastName(data.getOrDefault("lastName", trainee.getUser().getLastName()));
        request.setDateOfBirth(trainee.getDateOfBirth());
        request.setAddress(trainee.getAddress());
        request.setIsActive(trainee.getUser().isActive());

        String body = objectMapper.writeValueAsString(request);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                ).andReturn()
        );
    }

    @When("I deactivate trainee {string}")
    public void i_deactivate_trainee(String username) throws Exception {
        ActivationRequest request = new ActivationRequest();
        request.setUsername(username);
        request.setIsActive(false);

        String body = objectMapper.writeValueAsString(request);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/trainees/activate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @When("I request trainers list for trainee {string}")
    public void i_request_trainers_list_for_trainee(String username) throws Exception {
        currentTraineeUsername = username;

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/trainees/" + username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @When("I add trainer {string} to trainee {string}")
    public void i_add_trainer_to_trainee(String trainerUsername, String traineeUsername) throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("traineeUsername", traineeUsername);
        request.put("trainerUsernames", List.of(trainerUsername));

        String body = objectMapper.writeValueAsString(request);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/trainees/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @When("I remove trainer {string} from trainee {string}")
    public void i_remove_trainer_from_trainee(String trainerUsername, String traineeUsername) throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("traineeUsername", traineeUsername);
        request.put("trainerUsernames", List.of());

        String body = objectMapper.writeValueAsString(request);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/trainees/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                                .header("X-Password", "password123")
                ).andReturn()
        );
    }

    @Then("the trainee should be created successfully")
    public void the_trainee_should_be_created_successfully() {
        Trainee trainee = traineeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No trainee found"));
        assertThat(trainee.getUser()).isNotNull();
    }

    @Then("the trainee should be active")
    public void the_trainee_should_be_active() {
        Trainee trainee = traineeRepository.findAll().stream().findFirst().orElseThrow();
        assertThat(trainee.getUser().isActive()).isTrue();
    }

    @Then("the trainee profile should contain correct information")
    public void the_trainee_profile_should_contain_correct_information() throws Exception {
        String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(response).contains(currentTraineeUsername);
    }

    @Then("the trainee first name should be {string}")
    public void the_trainee_first_name_should_be(String firstName) {
        Trainee trainee = traineeRepository.findByUserUsername(currentTraineeUsername).orElseThrow();
        assertThat(trainee.getUser().getFirstName()).isEqualTo(firstName);
    }

    @Then("the trainee should be inactive")
    public void the_trainee_should_be_inactive() {
        Trainee trainee = traineeRepository.findByUserUsername(currentTraineeUsername).orElseThrow();
        assertThat(trainee.getUser().isActive()).isFalse();
    }

    @Then("the trainers list should contain {string}")
    public void the_trainers_list_should_contain(String trainerUsername) throws Exception {
        String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(response).contains(trainerUsername);
    }

    @Then("the trainee should have trainer {string}")
    public void the_trainee_should_have_trainer(String trainerUsername) {
        Trainee trainee = traineeRepository.findByUserUsername(currentTraineeUsername).orElseThrow();
        assertThat(trainee.getTrainers().stream()
                .anyMatch(t -> t.getUser().getUsername().equals(trainerUsername))).isTrue();
    }

    @Then("the trainee should not have trainer {string}")
    public void the_trainee_should_not_have_trainer(String trainerUsername) {
        Trainee trainee = traineeRepository.findByUserUsername(currentTraineeUsername).orElseThrow();
        assertThat(trainee.getTrainers().stream()
                .noneMatch(t -> t.getUser().getUsername().equals(trainerUsername))).isTrue();
    }
}