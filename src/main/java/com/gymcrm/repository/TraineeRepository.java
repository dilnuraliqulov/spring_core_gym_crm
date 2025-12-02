package com.gymcrm.repository;

import com.gymcrm.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    List<Trainee> findByUserIsActive(boolean isActive);

    // Check if a username exists
    boolean existsByUserUsername(String username);
}
