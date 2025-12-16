package com.gymcrm.service;

import com.gymcrm.entity.User;

import java.util.Optional;


public interface UserService extends AuthenticationService {


    Optional<User> findByUsername(String username);


    char[] hashPassword(char[] password);

    User save(User user);
}
