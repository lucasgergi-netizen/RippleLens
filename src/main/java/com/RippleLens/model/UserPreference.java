package com.RippleLens.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_preferences", uniqueConstraints = {
        @UniqueConstraint(columnNames = "sessionKey")
})
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionKey;

    @Enumerated(EnumType.STRING)
    private AssetCategory defaultCategory;

    private String defaultRangeKey;
    private String displayCurrency;
    private boolean showWelcomeScreen;
    private Instant updatedAt;

    public UserPreference() {
    }

    public UserPreference(String sessionKey) {
        this.sessionKey = sessionKey;
        this.defaultCategory = AssetCategory.METAL;
        this.defaultRangeKey = "30D";
        this.displayCurrency = "USD";
        this.showWelcomeScreen = true;
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public AssetCategory getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefaultCategory(AssetCategory defaultCategory) {
        this.defaultCategory = defaultCategory;
        touch();
    }

    public String getDefaultRangeKey() {
        return defaultRangeKey;
    }

    public void setDefaultRangeKey(String defaultRangeKey) {
        this.defaultRangeKey = defaultRangeKey;
        touch();
    }

    public String getDisplayCurrency() {
        return displayCurrency;
    }

    public void setDisplayCurrency(String displayCurrency) {
        this.displayCurrency = displayCurrency;
        touch();
    }

    public boolean isShowWelcomeScreen() {
        return showWelcomeScreen;
    }

    public void setShowWelcomeScreen(boolean showWelcomeScreen) {
        this.showWelcomeScreen = showWelcomeScreen;
        touch();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
}
