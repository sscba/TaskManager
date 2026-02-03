package com.taskmanager.taskmanagerapp.controller;

import com.taskmanager.taskmanagerapp.dto.request.RefreshTokenRequestDto;
import com.taskmanager.taskmanagerapp.dto.request.ResendVerificationRequestDTO;
import com.taskmanager.taskmanagerapp.dto.response.ApiResponseDTO;
import com.taskmanager.taskmanagerapp.dto.response.AuthResponseDTO;
import com.taskmanager.taskmanagerapp.dto.request.LoginRequestDTO;
import com.taskmanager.taskmanagerapp.dto.request.RegisterRequestDTO;
import com.taskmanager.taskmanagerapp.entity.RefreshToken;
import com.taskmanager.taskmanagerapp.security.CustomUserDetails;
import com.taskmanager.taskmanagerapp.service.AuthService;
import com.taskmanager.taskmanagerapp.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with USER role and returns JWT token for immediate login"
    )
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request){
        AuthResponseDTO response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates user credentials and returns JWT token valid for 24 hours"
    )
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request){
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Use refresh token to get new access token")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto){
        AuthResponseDTO response = authService.refreshToken(refreshTokenRequestDto.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "User logout", description = "Invalidates refresh token")
    public ResponseEntity<ApiResponseDTO> logout(@AuthenticationPrincipal CustomUserDetails userDetails){
        authService.logout(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponseDTO.success("Logged out successfully"));
    }

    @GetMapping("/verify-email")
    @Operation(
            summary = "Verify email address",
            description = "Called when user clicks the verification link in their email. Token is passed as query param."
    )
    public ResponseEntity<ApiResponseDTO> verifyEmail(
            @Parameter(description = "Verification token from email link", required = true, example = "abc-123-uuid")
            @RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponseDTO.success("Email verified successfully! You can now login."));
    }

    // ── Resend Verification Email ───────────────────────
    // User posts their email → we generate fresh token → send new email
    @PostMapping("/resend-verification")
    @Operation(
            summary = "Resend verification email",
            description = "Generates a new verification token and sends a fresh email. Previous links become invalid."
    )
    public ResponseEntity<ApiResponseDTO> resendVerification(
            @Valid @RequestBody ResendVerificationRequestDTO request) {
        emailVerificationService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(ApiResponseDTO.success("Verification email sent. Please check your inbox."));
    }

    // ── Check verification status ──────────────────────
    // Useful for frontend polling: "has the user verified yet?"
    @GetMapping("/verification-status")
    @Operation(
            summary = "Check email verification status",
            description = "Returns whether the given email has been verified"
    )
    public ResponseEntity<ApiResponseDTO> checkVerificationStatus(
            @Parameter(description = "Email to check", required = true, example = "user@example.com")
            @RequestParam String email) {
        boolean verified = emailVerificationService.isEmailVerified(email);
        String message = verified ? "Email is verified" : "Email is not yet verified";
        return ResponseEntity.ok(ApiResponseDTO.builder()
                .success(verified)
                .message(message)
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }
}
