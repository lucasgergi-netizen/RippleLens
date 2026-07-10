package com.RippleLens.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "portfolio_holdings", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "sessionKey", "asset_id" })
})
public class PortfolioHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionKey;

    @ManyToOne(optional = false)
    private Asset asset;

    private BigDecimal quantity;
    private BigDecimal averageCost;
    private Instant createdAt;
    private Instant updatedAt;

    public PortfolioHolding() {
    }

    public PortfolioHolding(String sessionKey, Asset asset, BigDecimal quantity, BigDecimal averageCost) {
        this.sessionKey = sessionKey;
        this.asset = asset;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Asset getAsset() {
        return asset;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        touch();
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
        touch();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
}
