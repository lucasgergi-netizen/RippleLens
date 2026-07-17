package com.RippleLens.repository;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.RippleLens.model.AppUser;
import com.RippleLens.model.PasswordResetToken;

import jakarta.persistence.LockModeType;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PasswordResetToken> findByTokenHashAndUsedAtIsNull(String tokenHash);

    List<PasswordResetToken> findByUserAndUsedAtIsNull(AppUser user);

    long deleteByExpiresAtBefore(Instant expiresAt);
}
