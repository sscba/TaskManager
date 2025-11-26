package com.taskmanager.taskmanagerapp.repository;

import com.taskmanager.taskmanagerapp.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDetails,Long> {
    Optional<UserDetails> findByUsername(String username);
    List<UserDetails> findByRole(String role);
    boolean existsByUsername(String username);
}
