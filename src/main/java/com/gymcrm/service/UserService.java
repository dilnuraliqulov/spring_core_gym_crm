package com.gymcrm.service;

import com.gymcrm.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    Optional<User> findByUsername(String username);

    char[] hashPassword(char[] password);

    boolean authenticate(String username, char[] password);

    void changePassword(String username, char[] currentPassword, char[] newPassword);

    void activate(User user);

    void deactivate(User user);


}
