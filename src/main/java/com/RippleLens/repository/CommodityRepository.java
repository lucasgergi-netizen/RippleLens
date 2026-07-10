package com.RippleLens.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RippleLens.model.Commodity;

public interface CommodityRepository
        extends JpaRepository<Commodity, Long> {

    Optional<Commodity> findBySymbolIgnoreCase(String symbol);
}
