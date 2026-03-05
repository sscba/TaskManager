package com.taskmanager.taskmanagerapp.service;

import com.taskmanager.taskmanagerapp.entity.EmailVerificationToken;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.exception.BadRequestException;
import com.taskmanager.taskmanagerapp.exception.ResourceNotFoundException;
import com.taskmanager.taskmanagerapp.repository.EmailVerificationTokenRepository;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${app.email.verification.expiration:86400000}")
    private Long tokenExpiration;

    @Value("${app.email.verification.base-url:http://localhost:8080}")
    private String baseUrl;

    // ── Called right after user registers ─────────────────────
    // Generates a token, saves it, and fires the email.
    @Async
    public void sendVerificationEmail(User user) {
        // Guard: already verified users don't need another email
        if (user.getEmailVerified()) {
            log.warn("User {} is already verified", user.getUsername());
            throw new BadRequestException("Email is already verified");
        }

        EmailVerificationToken token = generateToken(user);
        String verificationUrl = buildVerificationUrl(token.getToken());

        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationUrl);
        log.info("Verification email sent to user: {}", user.getUsername());
    }

    // ── Called when user clicks "Resend" ─────────────────────
    // Invalidates old tokens, creates a fresh one, sends email.
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));

        if (user.getEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }

        // Mark all previous tokens for this user as used
        // so only the new one is valid
        tokenRepository.findTopByUserOrderByCreatedAtDesc(user)
                .ifPresent(oldToken -> {
                    oldToken.setUsed(true);
                    tokenRepository.save(oldToken);
                });

        EmailVerificationToken newToken = generateToken(user);
        String verificationUrl = buildVerificationUrl(newToken.getToken());

        emailService.sendResendVerificationEmail(user.getEmail(), user.getUsername(), verificationUrl);
        log.info("Resend verification email sent to user: {}", user.getUsername());
    }

    // ── Called when user clicks the link in their email ──────
    // Validates token → marks token as used → flips emailVerified
    @Transactional
    public void verifyEmail(String token) {
        // 1. Find the token row
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification link"));

        // 2. Check it is still usable (not used AND not expired)
        if (!verificationToken.isValid()) {
            if (verificationToken.isExpired()) {
                throw new BadRequestException("Verification link has expired. Please request a new one.");
            }
            throw new BadRequestException("Verification link has already been used");
        }

        // 3. Flip the user's emailVerified flag
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // 4. Mark the token as consumed so it can't be replayed
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        log.info("Email verified successfully for user: {}", user.getUsername());
    }

    // ── Check if an email is already verified ───────────────
    @Transactional(readOnly = true)
    public boolean isEmailVerified(String email) {
        return userRepository.findByEmail(email)
                .map(User::getEmailVerified)
                .orElse(false);
    }

    // ── Cleanup: remove all expired tokens from DB ──────────
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Expired verification tokens cleaned up");
    }

    // ── Private helpers ──────────────────────────────────────

    // Creates and persists a new token for the given user
    private EmailVerificationToken generateToken(User user) {
        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(UUID.randomUUID().toString())   // random unique string
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(tokenExpiration))
                .used(false)
                .build();
        return tokenRepository.save(token);
    }

    // Builds the full URL that goes inside the email
    // e.g. http://localhost:8080/api/auth/verify-email?token=abc-123
    private String buildVerificationUrl(String token) {
        return baseUrl + "/api/auth/verify-email?token=" + token;
    }
}
