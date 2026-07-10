package com.RippleLens.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String symbol;

    @Enumerated(EnumType.STRING)
    private AssetCategory category;

    private BigDecimal currentPrice;
    private String quoteCurrency;
    private String unit;
    private BigDecimal dailyChangePercentage;
    private String description;

    public Asset() {
    }

    public Asset(
            String name,
            String symbol,
            AssetCategory category,
            BigDecimal currentPrice,
            String quoteCurrency,
            String unit,
            BigDecimal dailyChangePercentage,
            String description) {
        this.name = name;
        this.symbol = symbol;
        this.category = category;
        this.currentPrice = currentPrice;
        this.quoteCurrency = quoteCurrency;
        this.unit = unit;
        this.dailyChangePercentage = dailyChangePercentage;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getDailyChangePercentage() {
        return dailyChangePercentage;
    }

    public void setDailyChangePercentage(BigDecimal dailyChangePercentage) {
        this.dailyChangePercentage = dailyChangePercentage;
    }

    public String getDescription() {
        return description;
    }
}
