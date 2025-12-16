package com.gymcrm.service;


public interface AuthenticationService {


    boolean authenticate(String username, char[] password);

    void changePassword(String username, char[] currentPassword, char[] newPassword);
}

