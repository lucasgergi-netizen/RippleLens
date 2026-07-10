package com.ripplelens.marketdata;

import java.util.List;

public record MarketPriceSeries(
        List<MarketPricePoint> prices,
        String dataSource,
        boolean live
) {
}
