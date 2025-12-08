package com.gymcrm.service.impl;

import com.gymcrm.entity.User;
import com.gymcrm.exception.UserNotFoundException;
import com.gymcrm.repository.UserRepository;
import com.gymcrm.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .or(() -> {
                    throw new UserNotFoundException("User not found: " + username);
                });
    }

    @Override
    public boolean authenticate(String username, char[] password) {
        User user = userRepository.findByUsername(username)
              .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (!user.isActive()) {
            throw new DisabledException("User " + username + " is disabled");
        }

        if (!passwordEncoder.matches(new String(password), new String(user.getPassword()))) {
            throw new BadCredentialsException("Invalid password for username: " + username);
        }

        return true;
    }


    @Override
    public void changePassword(String username, char[] currentPassword, char[] newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(new String(currentPassword), new String(user.getPassword()))) {
            throw new BadCredentialsException("Invalid current password for username: " + username);
        }

        user.setPassword(passwordEncoder.encode(new String(newPassword)).toCharArray());
        userRepository.save(user);
    }

    @Override
    public char[] hashPassword(char[] password) {
        return passwordEncoder.encode(new String(password)).toCharArray();
    }

    @Override
    public void activate(User user) {
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void deactivate(User user) {
        user.setActive(false);
        userRepository.save(user);
    }


}
