package com.sapir.smartvacationplanner.repository;

import com.sapir.smartvacationplanner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
}