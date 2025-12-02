package com.gymcrm.service;

import com.gymcrm.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User findByUserName(String username);

    boolean authenticate(String username, char[] password);

    boolean matches(char[] password, char[] hashedPassword);

    char[] hashPassword(char[] password);

    void changePassword(String username,char[] currentPassword, char[] newPassword);
}
