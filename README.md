# RippleLens

RippleLens is a Spring Boot finance and economics MVP that analyzes metals, cryptocurrencies, and currencies. It goes beyond a price dashboard by combining asset categories, historical charts, live API data, stored observations, rule-based explanations, and causal ripple maps.

## Current Status

Completed phases:

- Phase 1: Core Spring Boot app structure
- Phase 2: Finished dashboard UI
- Phase 3: Real API data for crypto and currencies
- Phase 4: Stored historical observations and scheduled refresh
- Phase 5: Intelligent rule-based explanations
- Phase 6: Causal ripple maps for metals, crypto, and currencies
- Phase 7: Expanded live assets for top crypto, major currencies, and AED
- Phase 8: Asset comparison mode
- Phase 9: Macro indicator context
- Phase 10: Event-watch intelligence
- Phase 11: Persistent H2 database
- Phase 12: Market-terminal style chart upgrade
- Phase 13: Full fintech-terminal frontend redesign
- Phase 14: Shareable asset detail URLs
- Phase 15: Binance Spot crypto data with CoinGecko fallback
- Phase 16: Stronger explainable analysis summaries
- Phase 17: Database-backed watchlist and favorites
- Phase 18: Multi-condition database-backed asset alerts
- Phase 19: Data trust report
- Phase 20: Demo mode and presentation polish
- Phase 21: Full dark-terminal visual refinement

## Main Features

- Asset categories:
  - Metals: gold, silver, copper, platinum, palladium
  - Cryptocurrencies: BTC, ETH, USDT, BNB, USDC, XRP, SOL, TRX, FIGR_HELOC, HYPE
  - Currencies: USD, EUR, JPY, GBP, CNY, CAD, AUD, CHF, HKD, SGD, SEK, AED
- 7-day and 30-day price charts
- Hourly chart mode using a clearly labeled modeled 24-hour path
- Market-terminal style chart with dark grid, filled price area, right-side price axis, latest-price tag, hover tooltip, and high/low/move stat strip
- Professional terminal-style interface with sidebar navigation, live market tape, selected-asset command bar, and clearer dashboard sections
- Shareable asset views such as `/?asset=BTC&category=CRYPTOCURRENCY`
- Binance Spot historical daily crypto observations, with CoinGecko fallback when available
- Stronger explanation summaries that classify move size and pressure channels
- Database-backed watchlist for saving multiple metals, currencies, and crypto assets without duplicates
- Multi-condition alerts with active, triggered, and disabled states
- Data trust report showing source labels, observation count, caveats, and educational-use status
- Demo mode button for quickly cycling through showcase assets
- Consistent dark-terminal color system with no white dashboard panels
- Normalized comparison chart for two assets
- Increase/decrease movement cards showing absolute and percentage change
- Live crypto prices from Binance Spot, with CoinGecko fallback
- Live currency rates from Frankfurter
- Live metal prices from Gold API
- Live top-crypto prices from Binance Spot, with CoinGecko fallback
- Live major-currency rates from Frankfurter
- Daily stored price observations
- Manual live-data refresh endpoint
- Scheduled refresh every 6 hours
- Rule-based economic insight engine
- Causal ripple maps
- Macro context using World Bank indicators
- Event-watch signals for supply-chain, central-bank, regulation, trade, and geopolitical risks
- Persistent file-based H2 database at `./data/ripplelens`
- Confidence labels and financial-advice disclaimer

## APIs Used

- Binance Spot API for live crypto prices; CoinGecko Simple Price API as fallback.
- Frankfurter API for no-key currency exchange rates.
- ExchangeRate API for no-key UAE dirham rates.
- Gold API for no-key gold, silver, copper, platinum, and palladium prices.
- World Bank API for no-key macroeconomic indicators.

RippleLens maps the app symbol `XCU` to Gold API's copper symbol `HG` so the UI can keep one consistent internal symbol.

## Important Endpoints

```text
GET /api/assets
GET /api/assets/category/METAL
GET /api/assets/category/CRYPTOCURRENCY
GET /api/assets/category/CURRENCY
GET /api/assets/BTC/prices
GET /api/assets/BTC/prices/hourly
GET /api/live/metals
GET /api/live/cryptos
GET /api/live/currencies
GET /api/live/refresh
GET /api/insights/BTC?days=7
GET /api/metals/BTC/ripple-map
GET /api/intelligence/macro
GET /api/intelligence/macro/BTC
GET /api/intelligence/events/BTC
```

## How To Run

In Spring Tools:

1. Open `RippleLensApplication.java`.
2. Right-click inside the file.
3. Choose `Run As > Spring Boot App`.
4. Open `http://localhost:8080`.

From terminal:

```bash
./mvnw spring-boot:run
```

The database is now persistent. Restarting the app keeps stored observations in the local `data` folder.

## Project Explanation

RippleLens is not a trading-advice app. It is an economic intelligence tool. It helps users understand possible relationships between asset prices, monetary conditions, risk appetite, industrial demand, and consumer/business consequences.

Example:

```text
Bitcoin decreased over the selected window. RippleLens investigates crypto risk appetite and liquidity expectations as possible drivers, then highlights consequences for broader speculative sentiment.
```

## Trust And Safety

RippleLens avoids guaranteed investment predictions. Its explanations are educational, rule-based, and labeled with confidence levels. The app should always separate:

- observed price movement
- possible causes
- possible consequences
- uncertainty
- data source
