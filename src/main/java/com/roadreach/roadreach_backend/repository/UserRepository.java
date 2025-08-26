package com.roadreach.roadreach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.roadreach.roadreach_backend.model.User;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsByUserid(String userid);
    Optional<User> findByEmailAndPassword(String email, String password);

    @Transactional
    void deleteByUserid(String userid);
}


