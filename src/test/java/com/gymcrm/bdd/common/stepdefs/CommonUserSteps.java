package com.gymcrm.bdd.common.stepdefs;

import com.gymcrm.entity.User;
import com.gymcrm.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

public class CommonUserSteps {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CommonUserSteps(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Given("the following users exist:")
    public void the_following_users_exist(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String username = row.get("username");

            if (userRepository.findByUsername(username).isPresent()) {
                continue;
            }

            User user = new User();
            user.setUsername(username);
            // FIX: BCrypt-encode the password so login attempts with X-Password or /api/auth/login work
            user.setPassword(
                    passwordEncoder.encode(row.getOrDefault("password", "password123")).toCharArray()
            );
            user.setFirstName(row.getOrDefault("firstName", "Test"));
            user.setLastName(row.getOrDefault("lastName", "User"));
            user.setActive(Boolean.parseBoolean(row.getOrDefault("active", "true")));

            userRepository.save(user);
        }
    }
}