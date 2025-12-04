package com.gymcrm.service.impl;

import com.gymcrm.entity.User;
import com.gymcrm.exception.UserNotFoundException;
import com.gymcrm.repository.UserRepository;
import com.gymcrm.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.of(
                userRepository.findByUsername(username)
                       .orElseThrow(() ->
                               new UserNotFoundException(("User not found") + username)));
    }

    @Override
    public boolean authenticate(String username, char[] password) {
        return false;
    }

    @Override
    public boolean matches(char[] password, char[] hashedPassword) {
        return false;
    }

    @Override
    public char[] hashPassword(char[] password) {
        return new char[0];
    }

    @Override
    public void changePassword(String username, char[] currentPassword, char[] newPassword) {

    }

}
