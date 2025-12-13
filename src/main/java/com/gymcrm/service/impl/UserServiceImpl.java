package com.gymcrm.service.impl;

import com.gymcrm.entity.User;
import com.gymcrm.exception.UserNotFoundException;
import com.gymcrm.repository.UserRepository;
import com.gymcrm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, char[] password) {
        log.debug("Authenticating user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (!user.isActive()) {
            log.warn("Authentication failed - user is disabled: {}", username);
            throw new DisabledException("User " + username + " is disabled");
        }

        if (!passwordEncoder.matches(new String(password), new String(user.getPassword()))) {
            log.warn("Authentication failed - invalid password for user: {}", username);
            throw new BadCredentialsException("Invalid password for username: " + username);
        }

        log.info("User authenticated successfully: {}", username);
        return true;
    }

    @Override
    public void changePassword(String username, char[] currentPassword, char[] newPassword) {
        log.info("Changing password for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(new String(currentPassword), new String(user.getPassword()))) {
            log.warn("Password change failed - invalid current password for user: {}", username);
            throw new BadCredentialsException("Invalid current password for username: " + username);
        }

        user.setPassword(passwordEncoder.encode(new String(newPassword)).toCharArray());
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", username);
    }

    @Override
    public char[] hashPassword(char[] password) {
        return passwordEncoder.encode(new String(password)).toCharArray();
    }

    @Override
    public User save(User user) {
        log.debug("Saving user: {}", user.getUsername());
        return userRepository.save(user);
    }
}
