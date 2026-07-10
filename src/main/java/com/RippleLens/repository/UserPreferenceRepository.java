package com.RippleLens.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RippleLens.model.UserPreference;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    Optional<UserPreference> findBySessionKey(String sessionKey);
}
