package com.ripplelens;

import com.ripplelens.asset.Asset;
import com.ripplelens.asset.AssetRepository;
import com.ripplelens.asset.AssetType;
import com.ripplelens.price.PricePoint;
import com.ripplelens.price.PricePointRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RippleLensApplication {

    public static void main(String[] args) {
        SpringApplication.run(RippleLensApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDemoData(AssetRepository assets, PricePointRepository prices) {
        return args -> {
            Asset gold = assets.save(new Asset("XAU", "Gold", AssetType.METAL, "Safe-haven and inflation-sensitive metal"));
            Asset silver = assets.save(new Asset("XAG", "Silver", AssetType.METAL, "Precious and industrial metal used in solar panels and electronics"));
            Asset platinum = assets.save(new Asset("XPT", "Platinum", AssetType.METAL, "Industrial metal exposed to vehicle and hydrogen technology demand"));
            Asset palladium = assets.save(new Asset("XPD", "Palladium", AssetType.METAL, "Auto-catalyst metal exposed to supply shocks and EV substitution"));

            Asset usd = assets.save(new Asset("USD", "US Dollar", AssetType.FIAT_CURRENCY, "Global reserve currency"));
            Asset eur = assets.save(new Asset("EUR", "Euro", AssetType.FIAT_CURRENCY, "Major global currency"));
            Asset gbp = assets.save(new Asset("GBP", "British Pound", AssetType.FIAT_CURRENCY, "Major global currency"));
            Asset jpy = assets.save(new Asset("JPY", "Japanese Yen", AssetType.FIAT_CURRENCY, "Major safe-haven currency"));
            Asset chf = assets.save(new Asset("CHF", "Swiss Franc", AssetType.FIAT_CURRENCY, "Safe-haven currency"));
            Asset cad = assets.save(new Asset("CAD", "Canadian Dollar", AssetType.FIAT_CURRENCY, "Commodity-linked currency"));
            Asset aud = assets.save(new Asset("AUD", "Australian Dollar", AssetType.FIAT_CURRENCY, "Commodity-linked currency"));
            Asset cny = assets.save(new Asset("CNY", "Chinese Yuan", AssetType.FIAT_CURRENCY, "Large trade and industrial demand currency"));
            Asset inr = assets.save(new Asset("INR", "Indian Rupee", AssetType.FIAT_CURRENCY, "Important gold-consumption market currency"));
            Asset brl = assets.save(new Asset("BRL", "Brazilian Real", AssetType.FIAT_CURRENCY, "Emerging-market commodity currency"));

            Asset btc = assets.save(new Asset("BTC", "Bitcoin", AssetType.CRYPTO, "Crypto risk and digital scarcity signal"));
            Asset eth = assets.save(new Asset("ETH", "Ethereum", AssetType.CRYPTO, "Large smart-contract crypto asset"));
            Asset bnb = assets.save(new Asset("BNB", "BNB", AssetType.CRYPTO, "Large exchange ecosystem crypto asset"));
            Asset sol = assets.save(new Asset("SOL", "Solana", AssetType.CRYPTO, "High-beta smart-contract crypto asset"));
            Asset xrp = assets.save(new Asset("XRP", "XRP", AssetType.CRYPTO, "Payments-focused crypto asset"));
            Asset usdc = assets.save(new Asset("USDC", "USD Coin", AssetType.CRYPTO, "Stablecoin liquidity signal"));
            Asset doge = assets.save(new Asset("DOGE", "Dogecoin", AssetType.CRYPTO, "Retail speculation signal"));
            Asset ada = assets.save(new Asset("ADA", "Cardano", AssetType.CRYPTO, "Smart-contract crypto asset"));
            Asset trx = assets.save(new Asset("TRX", "TRON", AssetType.CRYPTO, "Stablecoin transfer ecosystem signal"));
            Asset avax = assets.save(new Asset("AVAX", "Avalanche", AssetType.CRYPTO, "High-beta smart-contract crypto asset"));

            List<Asset> allAssets = List.of(gold, silver, platinum, palladium, usd, eur, gbp, jpy, chf, cad, aud, cny, inr, brl,
                    btc, eth, bnb, sol, xrp, usdc, doge, ada, trx, avax);

            for (Asset asset : allAssets) {
                seedPriceSeries(prices, asset);
            }
        };
    }

    private void seedPriceSeries(PricePointRepository prices, Asset asset) {
        BigDecimal base = switch (asset.getSymbol()) {
            case "XAU" -> BigDecimal.valueOf(2350);
            case "XAG" -> BigDecimal.valueOf(31);
            case "XPT" -> BigDecimal.valueOf(1020);
            case "XPD" -> BigDecimal.valueOf(950);
            case "BTC" -> BigDecimal.valueOf(64000);
            case "ETH" -> BigDecimal.valueOf(3400);
            case "BNB" -> BigDecimal.valueOf(590);
            case "SOL" -> BigDecimal.valueOf(145);
            case "XRP" -> BigDecimal.valueOf(0.52);
            case "USDC" -> BigDecimal.valueOf(1);
            case "DOGE" -> BigDecimal.valueOf(0.12);
            case "ADA" -> BigDecimal.valueOf(0.42);
            case "TRX" -> BigDecimal.valueOf(0.13);
            case "AVAX" -> BigDecimal.valueOf(29);
            default -> BigDecimal.valueOf(1);
        };

        for (int i = 29; i >= 0; i--) {
            double wave = Math.sin(i / 4.0) * 0.018;
            double drift = (29 - i) * 0.0015;
            double multiplier = 1 + wave + drift;
            prices.save(new PricePoint(asset, LocalDate.now().minusDays(i), base.multiply(BigDecimal.valueOf(multiplier))));
        }
    }
}
