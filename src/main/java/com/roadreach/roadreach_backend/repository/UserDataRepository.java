package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface UserDataRepository extends JpaRepository<UserData, String> {
    Optional<UserData> findByUserid(String userid);
    boolean existsByUserid(String userid);

    @Transactional
    void deleteByUserid(String userid);
}