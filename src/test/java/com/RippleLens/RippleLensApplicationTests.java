package com.RippleLens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import com.RippleLens.alert.AlertResponse;
import com.RippleLens.alert.CreateAlertRequest;
import com.RippleLens.alert.UpdateAlertRequest;
import com.RippleLens.model.AlertType;
import com.RippleLens.model.AssetCategory;
import com.RippleLens.model.NotificationPreference;
import com.RippleLens.repository.AssetRepository;
import com.RippleLens.service.TrackingService;
import com.RippleLens.watchlist.CreateWatchlistItemRequest;

@SpringBootTest
class RippleLensApplicationTests {

	@Autowired
	private AssetRepository assetRepository;

	@Autowired
	private TrackingService trackingService;

	@Test
	void contextLoads() {
	}

	@Test
	void seedsTetherAsUsdtCryptoAsset() {
		assertThat(assetRepository.findBySymbolIgnoreCase("USDT"))
				.isPresent()
				.get()
				.satisfies(asset -> {
					assertThat(asset.getName()).isEqualTo("Tether");
					assertThat(asset.getCategory()).isEqualTo(AssetCategory.CRYPTOCURRENCY);
				});
	}

	@Test
	void supportsMultipleWatchlistAssetsAndMultipleAlertsForSameAsset() {
		trackingService.addWatchlistItem(new CreateWatchlistItemRequest("XAU"));
		trackingService.addWatchlistItem(new CreateWatchlistItemRequest("BTC"));
		trackingService.addWatchlistItem(new CreateWatchlistItemRequest("AED"));

		assertThat(trackingService.getWatchlist())
				.extracting("symbol")
				.contains("XAU", "BTC", "AED");

		trackingService.createAlert(new CreateAlertRequest(
				"BTC",
				AlertType.PRICE_ABOVE,
				BigDecimal.ONE,
				NotificationPreference.IN_APP,
				false,
				"BTC tiny threshold"));
		trackingService.createAlert(new CreateAlertRequest(
				"BTC",
				AlertType.PRICE_BELOW,
				BigDecimal.valueOf(999999),
				NotificationPreference.EMAIL,
				true,
				"BTC high ceiling"));
		trackingService.createAlert(new CreateAlertRequest(
				"BTC",
				AlertType.PERCENT_INCREASE_REACHES,
				BigDecimal.valueOf(5),
				NotificationPreference.BOTH,
				true,
				"BTC up 5"));

		assertThat(trackingService.getAlerts("BTC", null)).hasSizeGreaterThanOrEqualTo(3);
	}

	@Test
	void rejectsExactDuplicateAlertsWhenCreatingOrEditing() {
		CreateAlertRequest originalRequest = new CreateAlertRequest(
				"ETH",
				AlertType.PERCENT_INCREASE_REACHES,
				BigDecimal.valueOf(12),
				NotificationPreference.IN_APP,
				true,
				"ETH duplicate guard");
		AlertResponse original = trackingService.createAlert(originalRequest);

		assertThatThrownBy(() -> trackingService.createAlert(originalRequest))
				.isInstanceOf(ResponseStatusException.class)
				.hasMessageContaining("409 CONFLICT");

		AlertResponse second = trackingService.createAlert(new CreateAlertRequest(
				"ETH",
				AlertType.PRICE_ABOVE,
				BigDecimal.valueOf(9000),
				NotificationPreference.IN_APP,
				false,
				"ETH temporary different alert"));

		assertThatThrownBy(() -> trackingService.updateAlert(second.id(), new UpdateAlertRequest(
				original.alertType(),
				original.targetValue(),
				original.notificationPreference(),
				original.recurring(),
				true,
				original.customName())))
				.isInstanceOf(ResponseStatusException.class)
				.hasMessageContaining("409 CONFLICT");
	}

}
