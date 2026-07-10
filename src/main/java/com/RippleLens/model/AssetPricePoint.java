package com.RippleLens.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "asset_price_points")
public class AssetPricePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Asset asset;

    private LocalDate observationDate;
    private BigDecimal price;
    private String source;

    public AssetPricePoint() {
    }

    public AssetPricePoint(Asset asset, LocalDate observationDate, BigDecimal price) {
        this(asset, observationDate, price, "Demo");
    }

    public AssetPricePoint(Asset asset, LocalDate observationDate, BigDecimal price, String source) {
        this.asset = asset;
        this.observationDate = observationDate;
        this.price = price;
        this.source = source;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
