package com.RippleLens.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "watchlist_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "sessionKey", "asset_id" })
})
public class WatchlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionKey;

    @ManyToOne(optional = false)
    private Asset asset;

    private Instant createdAt;

    public WatchlistItem() {
    }

    public WatchlistItem(String sessionKey, Asset asset) {
        this.sessionKey = sessionKey;
        this.asset = asset;
        this.createdAt = Instant.now();
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

    public Instant getCreatedAt() {
        return createdAt;
    }
}
