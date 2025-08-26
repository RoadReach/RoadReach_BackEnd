package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserDataRepository extends JpaRepository<UserData, String> {
    Optional<UserData> findByUserid(String userid);
}