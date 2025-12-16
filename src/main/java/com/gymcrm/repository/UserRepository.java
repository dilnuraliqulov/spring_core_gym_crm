package com.gymcrm.repository;

import com.gymcrm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByActive(boolean active);

    boolean existsByUsername(String username);
}
