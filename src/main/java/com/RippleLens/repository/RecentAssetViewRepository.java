package com.RippleLens.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RippleLens.model.Asset;
import com.RippleLens.model.RecentAssetView;

public interface RecentAssetViewRepository extends JpaRepository<RecentAssetView, Long> {

    List<RecentAssetView> findTop8BySessionKeyOrderByViewedAtDesc(String sessionKey);

    Optional<RecentAssetView> findBySessionKeyAndAsset(String sessionKey, Asset asset);
}
