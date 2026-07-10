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
@Table(name = "recent_asset_views", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "sessionKey", "asset_id" })
})
public class RecentAssetView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionKey;

    @ManyToOne(optional = false)
    private Asset asset;

    private Instant viewedAt;

    public RecentAssetView() {
    }

    public RecentAssetView(String sessionKey, Asset asset) {
        this.sessionKey = sessionKey;
        this.asset = asset;
        this.viewedAt = Instant.now();
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

    public Instant getViewedAt() {
        return viewedAt;
    }

    public void touch() {
        this.viewedAt = Instant.now();
    }
}
