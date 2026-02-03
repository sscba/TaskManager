package com.taskmanager.taskmanagerapp.repository;

import com.taskmanager.taskmanagerapp.entity.EmailVerificationToken;
import com.taskmanager.taskmanagerapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken,Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    // Get the most recent token for a given user (for resend logic)
    Optional<EmailVerificationToken> findTopByUserOrderByCreatedAtDesc(User user);

    // Hard-delete all tokens belonging to a user (cleanup on delete user)
    void deleteByUser(User user);

    // Bulk-delete expired tokens (run via scheduler or on-demand)
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
