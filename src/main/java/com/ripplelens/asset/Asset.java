package com.ripplelens.asset;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String name;

    @Enumerated(EnumType.STRING)
    private AssetType type;

    private String description;

    protected Asset() {
    }

    public Asset(String symbol, String name, AssetType type, String description) {
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public AssetType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
