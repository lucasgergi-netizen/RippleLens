package com.RippleLens.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetPricePoint;

public interface AssetPricePointRepository extends JpaRepository<AssetPricePoint, Long> {

    List<AssetPricePoint> findByAssetOrderByObservationDateAsc(Asset asset);

    Optional<AssetPricePoint> findByAssetAndObservationDate(Asset asset, LocalDate observationDate);

    Optional<AssetPricePoint> findTopByAssetAndObservationDateBeforeOrderByObservationDateDesc(
            Asset asset,
            LocalDate observationDate);

    Optional<AssetPricePoint> findTopByAssetOrderByObservationDateDesc(Asset asset);
}
