package com.roadreach.roadreach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.roadreach.roadreach_backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}

