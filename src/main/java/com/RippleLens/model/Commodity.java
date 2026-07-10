package com.RippleLens.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "commodities")
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String symbol;
    private BigDecimal price;
    private String currency;
    private String unit;
    private BigDecimal dailyChangePercentage;

    public Commodity() {
    }

    public Commodity(
            String name,
            String symbol,
            BigDecimal price,
            String currency,
            String unit,
            BigDecimal dailyChangePercentage) {

        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.currency = currency;
        this.unit = unit;
        this.dailyChangePercentage = dailyChangePercentage;
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

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getDailyChangePercentage() {
        return dailyChangePercentage;
    }
}