package com.RippleLens.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RippleLens.repository.PasswordResetTokenRepository;

@Service
public class SecurityMaintenanceService {

    private final PasswordResetTokenRepository tokenRepository;

    public SecurityMaintenanceService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(cron = "0 23 3 * * *", zone = "UTC")
    @Transactional
    public void removeExpiredPasswordResetTokens() {
        tokenRepository.deleteByExpiresAtBefore(Instant.now().minus(1, ChronoUnit.DAYS));
    }
}
