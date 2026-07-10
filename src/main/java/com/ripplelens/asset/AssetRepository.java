package com.ripplelens.asset;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByType(AssetType type);

    Optional<Asset> findBySymbolIgnoreCase(String symbol);
}
