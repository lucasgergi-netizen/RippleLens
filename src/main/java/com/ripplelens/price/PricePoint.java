package com.ripplelens.price;

import com.ripplelens.asset.Asset;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class PricePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Asset asset;

    private LocalDate observationDate;
    private BigDecimal priceUsd;

    protected PricePoint() {
    }

    public PricePoint(Asset asset, LocalDate observationDate, BigDecimal priceUsd) {
        this.asset = asset;
        this.observationDate = observationDate;
        this.priceUsd = priceUsd;
    }

    public Long getId() {
        return id;
    }

    public Asset getAsset() {
        return asset;
    }

    public LocalDate getObservationDate() {
        return observationDate;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }
}
