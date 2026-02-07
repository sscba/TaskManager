package com.taskmanager.taskmanagerapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "tasks")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    // ── NEW FIELD: tracks whether email has been verified ──
    // Defaults to false. Flipped to true once user clicks
    // the verification link in their email.
    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    // Tracks consecutive failed login attempts
    @Column(nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    // When the account got locked (null = not locked)
    @Column
    private LocalDateTime lockedAt;

    // When the lockout expires (null = not locked)
    @Column
    private LocalDateTime lockExpiresAt;

    // General account lock flag (for manual admin locks too)
    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;

    // Check if account is currently locked due to failed attempts
    public boolean isAccountLocked() {
        // If never locked, obviously not locked
        if (lockExpiresAt == null) {
            return false;
        }

        // If lockout expired, auto-unlock
        if (LocalDateTime.now().isAfter(lockExpiresAt)) {
            unlockAccount();
            return false;
        }

        // Still within lockout window
        return !accountNonLocked;
    }

    // Increment failed attempt counter
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }

    // Lock the account for a specific duration
    public void lockAccount(int durationMinutes) {
        this.accountNonLocked = false;
        this.lockedAt = LocalDateTime.now();
        this.lockExpiresAt = LocalDateTime.now().plusMinutes(durationMinutes);
    }

    // Reset account after successful login or manual unlock
    public void unlockAccount() {
        this.failedLoginAttempts = 0;
        this.accountNonLocked = true;
        this.lockedAt = null;
        this.lockExpiresAt = null;
    }

    // Get remaining lockout time in minutes
    public Long getRemainingLockoutMinutes() {
        if (lockExpiresAt == null || LocalDateTime.now().isAfter(lockExpiresAt)) {
            return 0L;
        }
        return java.time.Duration.between(LocalDateTime.now(), lockExpiresAt).toMinutes();
    }

}
