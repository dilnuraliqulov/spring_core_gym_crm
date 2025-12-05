package com.gymcrm.service.impl.hibernate;

import com.gymcrm.entity.User;
import com.gymcrm.exception.UserNotFoundException;
import com.gymcrm.repository.UserRepository;
import com.gymcrm.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.of(
                userRepository.findByUsername(username)
                       .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    public boolean authenticate(String username, char[] password) {
        User user = userRepository.findByUsername(username)
              .orElseThrow(() -> new UserNotFoundException("User not found" + username));

        if (!user.isActive()) {
            throw new DisabledException("User " + username + " is disabled");
        }

        if (!matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password for username: " + username);
        }

        return true;
    }

    @Override
    public boolean matches(char[] password, char[] hashedPassword) {

        return Arrays.equals(password, hashedPassword);
    }

    @Override
    public char[] hashPassword(char[] password) {
        return new char[0];
    }

    @Override
    public void changePassword(String username, char[] currentPassword, char[] newPassword) {

    }

}
