package com.RippleLens.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RippleLens.model.Asset;
import com.RippleLens.model.WatchlistItem;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    List<WatchlistItem> findBySessionKeyOrderByCreatedAtAsc(String sessionKey);

    Optional<WatchlistItem> findBySessionKeyAndAsset(String sessionKey, Asset asset);

    Optional<WatchlistItem> findByIdAndSessionKey(Long id, String sessionKey);

    boolean existsBySessionKeyAndAsset(String sessionKey, Asset asset);

    long countBySessionKey(String sessionKey);
}
