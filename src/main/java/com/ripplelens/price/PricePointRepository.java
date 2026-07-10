package com.ripplelens.price;

import com.ripplelens.asset.Asset;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricePointRepository extends JpaRepository<PricePoint, Long> {
    List<PricePoint> findByAssetOrderByObservationDateAsc(Asset asset);
}
