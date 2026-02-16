package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    Optional<PasswordResetCode> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
    void deleteByEmailAndCode(String email, String code);
}
