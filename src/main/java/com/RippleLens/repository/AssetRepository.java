package com.RippleLens.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByCategoryOrderByNameAsc(AssetCategory category);

    Optional<Asset> findBySymbolIgnoreCase(String symbol);
}
