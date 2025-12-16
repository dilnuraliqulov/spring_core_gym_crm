package com.gymcrm.service;

public interface ActivationService {

    void activate(String username);

    void deactivate(String username);

    boolean isActive(String username);
}

