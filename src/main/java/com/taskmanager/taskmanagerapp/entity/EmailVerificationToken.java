package com.taskmanager.taskmanagerapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The random string we embed in the email link.
    // Must be unique so no two tokens collide.
    @Column(nullable = false, unique = true)
    private String token;

    // Who this token belongs to.
    // One user can have multiple tokens (e.g. resend scenario),
    // but only the latest one matters.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Absolute deadline. After this time the token is dead.
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // Whether this token has already been used to verify.
    // Prevents replay: using the same link twice does nothing.
    @Column(nullable = false)
    @Builder.Default
    private Boolean used = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Utility: is this token still usable right now?
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // Utility: token is valid only when it hasn't expired AND hasn't been used
    public boolean isValid() {
        return !isExpired() && !used;
    }
}
