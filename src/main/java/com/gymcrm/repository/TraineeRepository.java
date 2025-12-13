package com.gymcrm.repository;

import com.gymcrm.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    List<Trainee> findByUserIsActive(boolean isActive);

    boolean existsByUserUsername(String username);

    void deleteByUserUsername(String username);

    @Query("SELECT DISTINCT u.username FROM User u")
    Set<String> findAllUsernames();
}
