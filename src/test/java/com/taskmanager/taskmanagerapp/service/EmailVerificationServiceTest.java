package com.taskmanager.taskmanagerapp.service;


import com.taskmanager.taskmanagerapp.entity.EmailVerificationToken;
import com.taskmanager.taskmanagerapp.entity.Role;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.exception.BadRequestException;
import com.taskmanager.taskmanagerapp.exception.ResourceNotFoundException;
import com.taskmanager.taskmanagerapp.repository.EmailVerificationTokenRepository;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailVerificationService Tests")
class EmailVerificationServiceTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private User unverifiedUser;
    private User verifiedUser;
    private EmailVerificationToken validToken;
    private EmailVerificationToken expiredToken;
    private EmailVerificationToken usedToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailVerificationService, "tokenExpiration", 86400L);
        ReflectionTestUtils.setField(emailVerificationService, "baseUrl", "http://localhost:8083");

        unverifiedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("new@test.com")
                .fullName("New User")
                .role(Role.USER)
                .emailVerified(false)
                .build();

        verifiedUser = User.builder()
                .id(2L)
                .username("verified")
                .email("verified@test.com")
                .fullName("Verified User")
                .role(Role.USER)
                .emailVerified(true)
                .build();

        validToken = EmailVerificationToken.builder()
                .id(1L)
                .token("valid-uuid-token")
                .user(unverifiedUser)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .build();

        expiredToken = EmailVerificationToken.builder()
                .id(2L)
                .token("expired-uuid-token")
                .user(unverifiedUser)
                .expiresAt(LocalDateTime.now().minusHours(1))  // already past
                .used(false)
                .build();

        usedToken = EmailVerificationToken.builder()
                .id(3L)
                .token("used-uuid-token")
                .user(unverifiedUser)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(true)   // already consumed
                .build();
    }

    @Nested
    @DisplayName("Send Verification Email Tests")
    class SendVerificationEmailTests {

        @Test
        @DisplayName("Should generate token and send email for unverified user")
        void sendVerificationEmail_WhenUnverified_ShouldSendEmail() {
            when(tokenRepository.save(any())).thenReturn(validToken);

            emailVerificationService.sendVerificationEmail(unverifiedUser);

            verify(tokenRepository).save(any(EmailVerificationToken.class));
            verify(emailService).sendVerificationEmail(
                    eq("new@test.com"),
                    eq("newuser"),
                    argThat(url -> url.contains("/api/auth/verify-email?token="))
            );
        }

        @Test
        @DisplayName("Should throw BadRequestException when user is already verified")
        void sendVerificationEmail_WhenAlreadyVerified_ShouldThrowException() {
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> emailVerificationService.sendVerificationEmail(verifiedUser)
            );

            assertThat(ex.getMessage()).contains("already verified");
            verify(emailService, never()).sendVerificationEmail(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Resend Verification Tests")
    class ResendVerificationTests {

        @Test
        @DisplayName("Should invalidate old token and send new email")
        void resendVerificationEmail_WhenUnverified_ShouldResend() {
            when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.of(unverifiedUser));
            when(tokenRepository.findTopByUserOrderByCreatedAtDesc(unverifiedUser)).thenReturn(Optional.of(validToken));
            when(tokenRepository.save(any())).thenReturn(validToken);

            emailVerificationService.resendVerificationEmail("new@test.com");

            // Old token should be marked used
            assertThat(validToken.getUsed()).isTrue();
            verify(emailService).sendResendVerificationEmail(any(), any(), any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when email not registered")
        void resendVerificationEmail_WhenEmailNotFound_ShouldThrowException() {
            when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> emailVerificationService.resendVerificationEmail("ghost@test.com")
            );
        }

        @Test
        @DisplayName("Should throw BadRequestException when already verified")
        void resendVerificationEmail_WhenAlreadyVerified_ShouldThrowException() {
            when(userRepository.findByEmail("verified@test.com")).thenReturn(Optional.of(verifiedUser));

            assertThrows(
                    BadRequestException.class,
                    () -> emailVerificationService.resendVerificationEmail("verified@test.com")
            );
        }
    }

    @Nested
    @DisplayName("Verify Email Tests")
    class VerifyEmailTests {

        @Test
        @DisplayName("Should verify email successfully with valid token")
        void verifyEmail_WhenValidToken_ShouldVerifyUser() {
            when(tokenRepository.findByToken("valid-uuid-token")).thenReturn(Optional.of(validToken));
            when(userRepository.save(any())).thenReturn(unverifiedUser);
            when(tokenRepository.save(any())).thenReturn(validToken);

            emailVerificationService.verifyEmail("valid-uuid-token");

            // User's emailVerified must have been flipped
            assertThat(unverifiedUser.getEmailVerified()).isTrue();
            // Token must be marked used
            assertThat(validToken.getUsed()).isTrue();

            verify(userRepository).save(unverifiedUser);
            verify(tokenRepository).save(validToken);
        }

        @Test
        @DisplayName("Should throw BadRequestException when token does not exist")
        void verifyEmail_WhenTokenNotFound_ShouldThrowException() {
            when(tokenRepository.findByToken("fake-token")).thenReturn(Optional.empty());

            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> emailVerificationService.verifyEmail("fake-token")
            );

            assertThat(ex.getMessage()).contains("Invalid or expired");
        }

        @Test
        @DisplayName("Should throw BadRequestException when token is expired")
        void verifyEmail_WhenTokenExpired_ShouldThrowException() {
            when(tokenRepository.findByToken("expired-uuid-token")).thenReturn(Optional.of(expiredToken));

            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> emailVerificationService.verifyEmail("expired-uuid-token")
            );

            assertThat(ex.getMessage()).contains("expired");
        }

        @Test
        @DisplayName("Should throw BadRequestException when token is already used")
        void verifyEmail_WhenTokenAlreadyUsed_ShouldThrowException() {
            when(tokenRepository.findByToken("used-uuid-token")).thenReturn(Optional.of(usedToken));

            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> emailVerificationService.verifyEmail("used-uuid-token")
            );

            assertThat(ex.getMessage()).contains("already been used");
        }
    }

    @Nested
    @DisplayName("Verification Status Tests")
    class VerificationStatusTests {

        @Test
        @DisplayName("Should return true when email is verified")
        void isEmailVerified_WhenVerified_ShouldReturnTrue() {
            when(userRepository.findByEmail("verified@test.com")).thenReturn(Optional.of(verifiedUser));

            boolean result = emailVerificationService.isEmailVerified("verified@test.com");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when email is not verified")
        void isEmailVerified_WhenNotVerified_ShouldReturnFalse() {
            when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.of(unverifiedUser));

            boolean result = emailVerificationService.isEmailVerified("new@test.com");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when email does not exist")
        void isEmailVerified_WhenEmailNotFound_ShouldReturnFalse() {
            when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

            boolean result = emailVerificationService.isEmailVerified("ghost@test.com");

            assertThat(result).isFalse();
        }
    }

}
