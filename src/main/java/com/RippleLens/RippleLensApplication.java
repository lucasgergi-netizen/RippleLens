package com.RippleLens;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;

import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;
import com.RippleLens.model.AssetPricePoint;
import com.RippleLens.model.Commodity;
import com.RippleLens.repository.AssetPricePointRepository;
import com.RippleLens.repository.AssetRepository;
import com.RippleLens.repository.CommodityRepository;

@SpringBootApplication
@EnableScheduling
public class RippleLensApplication {

	public static void main(String[] args) {
		SpringApplication.run(RippleLensApplication.class, args);
	}

	@Bean
	CommandLineRunner seedDemoData(
			CommodityRepository commodityRepository,
			AssetRepository assetRepository,
			AssetPricePointRepository assetPricePointRepository) {
		return args -> {
			saveCommodityIfMissing(commodityRepository, new Commodity("Gold", "XAU", new BigDecimal("4050.00"), "USD", "troy ounce", new BigDecimal("0.00")));
			saveCommodityIfMissing(commodityRepository, new Commodity("Silver", "XAG", new BigDecimal("58.40"), "USD", "troy ounce", new BigDecimal("0.00")));
			saveCommodityIfMissing(commodityRepository, new Commodity("Platinum", "XPT", new BigDecimal("1585.00"), "USD", "troy ounce", new BigDecimal("0.00")));
			saveCommodityIfMissing(commodityRepository, new Commodity("Palladium", "XPD", new BigDecimal("1240.00"), "USD", "troy ounce", new BigDecimal("0.00")));

			List<Asset> assets = List.of(
					new Asset("Gold", "XAU", AssetCategory.METAL, new BigDecimal("4050.00"), "USD", "troy ounce", new BigDecimal("0.00"), "Safe-haven metal linked to inflation, rates, and currency confidence."),
					new Asset("Silver", "XAG", AssetCategory.METAL, new BigDecimal("58.40"), "USD", "troy ounce", new BigDecimal("0.00"), "Precious and industrial metal used in solar panels and electronics."),
					new Asset("Copper", "XCU", AssetCategory.METAL, new BigDecimal("5.99"), "USD", "pound", new BigDecimal("0.00"), "Industrial metal linked to construction, electrification, and manufacturing."),
					new Asset("Platinum", "XPT", AssetCategory.METAL, new BigDecimal("1585.00"), "USD", "troy ounce", new BigDecimal("0.00"), "Industrial precious metal linked to vehicle catalysts and hydrogen technology."),
					new Asset("Palladium", "XPD", AssetCategory.METAL, new BigDecimal("1240.00"), "USD", "troy ounce", new BigDecimal("0.00"), "Auto-catalyst metal exposed to Russia supply risk and EV substitution."),
					new Asset("Bitcoin", "BTC", AssetCategory.CRYPTOCURRENCY, new BigDecimal("61766.00"), "USD", "coin", new BigDecimal("0.00"), "Crypto asset used as a digital scarcity and risk-appetite signal."),
					new Asset("Ethereum", "ETH", AssetCategory.CRYPTOCURRENCY, new BigDecimal("1730.46"), "USD", "coin", new BigDecimal("0.00"), "Large smart-contract crypto asset."),
					new Asset("Tether", "USDT", AssetCategory.CRYPTOCURRENCY, new BigDecimal("0.9991"), "USD", "token", new BigDecimal("0.00"), "Large USD stablecoin used as crypto market liquidity and settlement infrastructure."),
					new Asset("BNB", "BNB", AssetCategory.CRYPTOCURRENCY, new BigDecimal("560.47"), "USD", "coin", new BigDecimal("0.00"), "Exchange-linked crypto asset connected to trading activity and blockchain ecosystem demand."),
					new Asset("USDC", "USDC", AssetCategory.CRYPTOCURRENCY, new BigDecimal("0.9998"), "USD", "token", new BigDecimal("0.00"), "Regulated USD stablecoin that can signal dollar liquidity inside crypto markets."),
					new Asset("XRP", "XRP", AssetCategory.CRYPTOCURRENCY, new BigDecimal("0.49"), "USD", "coin", new BigDecimal("0.00"), "Payments-focused crypto asset sensitive to regulation and cross-border settlement narratives."),
					new Asset("Solana", "SOL", AssetCategory.CRYPTOCURRENCY, new BigDecimal("145.00"), "USD", "coin", new BigDecimal("0.00"), "High-beta crypto asset that can reflect speculative risk appetite."),
					new Asset("TRON", "TRX", AssetCategory.CRYPTOCURRENCY, new BigDecimal("0.28"), "USD", "coin", new BigDecimal("0.00"), "Blockchain asset closely watched for stablecoin transfer activity and retail crypto flows."),
					new Asset("Figure Heloc", "FIGR_HELOC", AssetCategory.CRYPTOCURRENCY, new BigDecimal("1.03"), "USD", "token", new BigDecimal("0.00"), "Tokenized credit-linked crypto asset that connects digital assets with real-world finance."),
					new Asset("Hyperliquid", "HYPE", AssetCategory.CRYPTOCURRENCY, new BigDecimal("36.00"), "USD", "token", new BigDecimal("0.00"), "High-growth crypto asset linked to decentralized trading activity and speculative liquidity."),
					new Asset("US Dollar", "USD", AssetCategory.CURRENCY, new BigDecimal("1.00"), "USD", "currency index base", new BigDecimal("0.00"), "Global reserve currency and key driver of USD metal prices."),
					new Asset("Euro", "EUR", AssetCategory.CURRENCY, new BigDecimal("1.08"), "USD", "currency", new BigDecimal("0.18"), "Major global currency used to compare whether metal moves are dollar-specific."),
					new Asset("Japanese Yen", "JPY", AssetCategory.CURRENCY, new BigDecimal("0.0062"), "USD", "currency", new BigDecimal("0.00"), "Major Asian funding currency sensitive to Bank of Japan policy and carry trades."),
					new Asset("British Pound", "GBP", AssetCategory.CURRENCY, new BigDecimal("1.27"), "USD", "currency", new BigDecimal("0.00"), "Major global currency and cross-currency comparison signal."),
					new Asset("Chinese Yuan", "CNY", AssetCategory.CURRENCY, new BigDecimal("0.1472"), "USD", "currency", new BigDecimal("0.00"), "Major trade currency linked to Chinese growth, exports, and commodity demand."),
					new Asset("Canadian Dollar", "CAD", AssetCategory.CURRENCY, new BigDecimal("0.7033"), "USD", "currency", new BigDecimal("0.00"), "Commodity-sensitive currency linked to energy, trade, and North American growth."),
					new Asset("Australian Dollar", "AUD", AssetCategory.CURRENCY, new BigDecimal("0.6948"), "USD", "currency", new BigDecimal("0.00"), "Commodity-sensitive currency linked to China demand and global risk appetite."),
					new Asset("Swiss Franc", "CHF", AssetCategory.CURRENCY, new BigDecimal("1.2403"), "USD", "currency", new BigDecimal("0.00"), "Safe-haven currency linked to risk-off flows and European financial conditions."),
					new Asset("Hong Kong Dollar", "HKD", AssetCategory.CURRENCY, new BigDecimal("0.1275"), "USD", "currency", new BigDecimal("0.00"), "USD-linked Asian financial hub currency used in regional capital flows."),
					new Asset("Singapore Dollar", "SGD", AssetCategory.CURRENCY, new BigDecimal("0.7744"), "USD", "currency", new BigDecimal("0.00"), "Asian reserve-style currency linked to trade, inflation management, and regional growth."),
					new Asset("Swedish Krona", "SEK", AssetCategory.CURRENCY, new BigDecimal("0.1035"), "USD", "currency", new BigDecimal("0.00"), "European cyclical currency linked to rates, exports, and risk appetite."),
					new Asset("UAE Dirham", "AED", AssetCategory.CURRENCY, new BigDecimal("0.2723"), "USD", "currency", new BigDecimal("0.00"), "United Arab Emirates currency, closely linked to the US dollar and important for Gulf trade, energy, and remittance flows."));

			for (Asset asset : assets) {
				Asset savedAsset = assetRepository.findBySymbolIgnoreCase(asset.getSymbol())
						.orElseGet(() -> assetRepository.save(asset));
				if (assetPricePointRepository.findByAssetOrderByObservationDateAsc(savedAsset).isEmpty()) {
					seedPriceHistory(assetPricePointRepository, savedAsset);
				}
			}
		};
	}

	private void saveCommodityIfMissing(CommodityRepository commodityRepository, Commodity commodity) {
		if (commodityRepository.findBySymbolIgnoreCase(commodity.getSymbol()).isEmpty()) {
			commodityRepository.save(commodity);
		}
	}

	private void seedPriceHistory(AssetPricePointRepository assetPricePointRepository, Asset asset) {
		BigDecimal basePrice = asset.getCurrentPrice();
		for (int dayOffset = 29; dayOffset >= 0; dayOffset--) {
			double wave = Math.sin(dayOffset / 3.0) * 0.025;
			double trend = (29 - dayOffset) * trendFor(asset) / 100.0;
			double multiplier = 1 + wave + trend;
			BigDecimal price = basePrice.multiply(BigDecimal.valueOf(multiplier));
			assetPricePointRepository.save(new AssetPricePoint(asset, LocalDate.now().minusDays(dayOffset), price));
		}
	}

	private double trendFor(Asset asset) {
		return switch (asset.getCategory()) {
			case METAL -> 0.10;
			case CRYPTOCURRENCY -> 0.22;
			case CURRENCY -> 0.015;
		};
	}
}
