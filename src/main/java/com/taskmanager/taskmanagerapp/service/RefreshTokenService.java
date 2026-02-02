package com.taskmanager.taskmanagerapp.service;

import com.taskmanager.taskmanagerapp.entity.RefreshToken;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.exception.UnauthorizedException;
import com.taskmanager.taskmanagerapp.repository.RefreshTokenRepository;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh.expiration:180000}")
    private long refreshTokenDurationMS;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("User not found"));

        //delete old refresh token
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMS))
                .createdDate(Instant.now())
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token){
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.isExpired()){
            refreshTokenRepository.delete(token);
            new UnauthorizedException("Refresh token expired. Login again");
        }
        return token;
    }

    @Transactional
    public void deleteByUser(User user){
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void deleteByUserId(Long userId){
        refreshTokenRepository.deleteByUserId(userId);
    }
}
