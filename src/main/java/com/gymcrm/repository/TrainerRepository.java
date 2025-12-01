package com.gymcrm.repository;

import com.gymcrm.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    List<Trainer> findByUserIsActive(boolean isActive);

    // Check if a username exists
    boolean existsByUserUsername(String username);
}
