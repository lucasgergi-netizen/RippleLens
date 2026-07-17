package com.RippleLens.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.RippleLens.model.AppUser;

import jakarta.persistence.LockModeType;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmailIgnoreCase(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select appUser from AppUser appUser where lower(appUser.email) = lower(:email)")
    Optional<AppUser> findForLogin(@Param("email") String email);

    boolean existsByEmailIgnoreCase(String email);
}
