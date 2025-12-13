package com.gymcrm.repository;

import com.gymcrm.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    List<Trainer> findByUserIsActive(boolean isActive);

    boolean existsByUserUsername(String username);

    @Query("SELECT t FROM Trainer t WHERE t.user.isActive = true AND t NOT IN " +
           "(SELECT tr FROM Trainee te JOIN te.trainers tr WHERE te.user.username = :traineeUsername)")
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);

    List<Trainer> findByUserUsernameIn(List<String> usernames);
}
