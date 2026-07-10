package com.RippleLens.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RippleLens.model.Asset;
import com.RippleLens.model.PortfolioHolding;

public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding, Long> {

    List<PortfolioHolding> findBySessionKeyOrderByUpdatedAtDesc(String sessionKey);

    Optional<PortfolioHolding> findBySessionKeyAndAsset(String sessionKey, Asset asset);

    long countBySessionKey(String sessionKey);
}
