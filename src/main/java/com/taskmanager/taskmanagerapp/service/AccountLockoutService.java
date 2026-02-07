package com.taskmanager.taskmanagerapp.service;

import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.exception.UnauthorizedException;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountLockoutService {

    private final UserRepository userRepository;

    @Value("${account.lockout.max-attempts:5}")
    private int maxAttempts;

    @Value("${account.lockout.duration:30}")
    private int lockoutDurationMinutes;

    @Transactional
    public boolean recordFailedLoginAttempt(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            // Don't leak whether username exists - just log and return
            log.debug("Failed login attempt for non-existent user: {}", username);
            return false;
        }

        User user = userOpt.get();

        // If already locked, don't increment counter further
        if (user.isAccountLocked()) {
            log.warn("Login attempt on already locked account: {}", username);
            return true;
        }

        // Increment failed attempts counter
        user.incrementFailedAttempts();
        log.info("Failed login attempt {} of {} for user: {}",
                user.getFailedLoginAttempts(), maxAttempts, username);

        // Lock account if threshold reached
        if (user.getFailedLoginAttempts() >= maxAttempts) {
            user.lockAccount(lockoutDurationMinutes);
            userRepository.save(user);
            log.warn("Account locked due to {} failed attempts: {}. Lockout duration: {} minutes",
                    maxAttempts, username, lockoutDurationMinutes);
            return true;
        }

        userRepository.save(user);
        return false;
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            if (user.getFailedLoginAttempts() > 0 || !user.getAccountNonLocked()) {
                user.unlockAccount();
                userRepository.save(user);
                log.info("Failed login attempts reset for user: {}", username);
            }
        });
    }

    @Transactional(readOnly = true)
    public void checkAccountLockStatus(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            if (user.isAccountLocked()) {
                long remainingMinutes = user.getRemainingLockoutMinutes();
                throw new UnauthorizedException(
                        String.format("Account is locked due to multiple failed login attempts. " +
                                "Please try again in %d minutes.", remainingMinutes)
                );
            }
        });
    }

    @Transactional
    public void manuallyUnlockAccount(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.unlockAccount();
            userRepository.save(user);
            log.info("Account manually unlocked by admin: {}", username);
        });
    }

    @Transactional(readOnly = true)
    public LockoutStatus getLockoutStatus(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new LockoutStatus(
                        user.isAccountLocked(),
                        user.getFailedLoginAttempts(),
                        maxAttempts,
                        user.getRemainingLockoutMinutes()
                ))
                .orElse(new LockoutStatus(false, 0, maxAttempts, 0L));
    }

    // DTO for lockout status
    public record LockoutStatus(
            boolean locked,
            int failedAttempts,
            int maxAttempts,
            long remainingLockoutMinutes
    ) {}
}
