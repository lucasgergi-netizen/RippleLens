# RippleLens

RippleLens is a beginner-friendly Spring Boot finance app that explains metal price movement using metals, currencies, crypto assets, and economic signals.

This first version is an MVP with live-first market data and stored demo fallback data. It already has:

- Gold, silver, platinum, and palladium
- Top 10 fiat currencies
- Top 10 crypto assets
- Live crypto price history from Binance Spot API, with CoinGecko as fallback
- Live fiat currency history from Frankfurter
- Live latest metal prices from Gold API, with stored history as chart fallback
- API endpoints
- A simple web page
- A basic "why did it move?" explanation engine
- A basic Money Pressure Index

## 1. Best Tool To Use

Use **Spring Tools for Eclipse** first.

Why:

- It is made for Spring Boot.
- It is easier for beginners to run and debug Java projects.
- It understands Maven projects.
- It gives you buttons for running the app instead of requiring many terminal commands.

Use **VS Code later** if you build a bigger frontend with React.

## 2. Open The Project In Spring Tools

1. Open Spring Tools for Eclipse.
2. Click `File`.
3. Click `Import`.
4. Choose `Maven`.
5. Choose `Existing Maven Projects`.
6. Select this folder:

   `/Users/lucasgergi/Documents/intern RippleLens`

7. Click `Finish`.
8. Wait until dependencies finish downloading.

## 3. Run The App

In Spring Tools:

1. Open `src/main/java/com/ripplelens/RippleLensApplication.java`.
2. Right-click inside the file.
3. Click `Run As`.
4. Click `Spring Boot App`.

Then open this in your browser:

`http://localhost:8080`

## Deploy Online

For online deployment, use the Render setup in [DEPLOYMENT.md](DEPLOYMENT.md).

The project now includes:

- `Dockerfile`
- `render.yaml`
- `.dockerignore`
- `.env.example`
- `src/main/resources/application-prod.properties`

Render is the recommended free option because it can run the full Spring Boot app, including the API and the frontend.

## 4. Try The API

Open these links after the app starts:

- `http://localhost:8080/api/metals`
- `http://localhost:8080/api/currencies/top`
- `http://localhost:8080/api/cryptos/top`
- `http://localhost:8080/api/assets/XAU/prices`
- `http://localhost:8080/api/assets/XAU/insight`
- `http://localhost:8080/api/metals/XAU/money-pressure`

## 5. What Each Folder Does

```text
src/main/java/com/ripplelens
  RippleLensApplication.java        Starts the app and creates fallback demo data
  asset/                            Metals, fiat currencies, cryptos
  marketdata/                       Live API clients and stored-data fallback
  price/                            Historical price points
  insight/                          Explanation and Money Pressure logic
  api/                              REST API endpoints
  web/                              Web page controller

src/main/resources
  templates/index.html              Main page
  static/styles.css                 Page design
  static/app.js                     Frontend logic
  application.properties            App configuration and database settings
```

## 6. Beginner Development Roadmap

### Step 1: Understand The MVP

Goal: know what the app already does.

Read these files in this order:

1. `RippleLensApplication.java`
2. `Asset.java`
3. `PricePoint.java`
4. `RippleLensApiController.java`
5. `InsightService.java`
6. `index.html`
7. `app.js`

### Step 2: Add More Metals

Add metals such as copper, nickel, lithium, zinc, and aluminum inside `RippleLensApplication.java`.

Example:

```java
Asset copper = assets.save(new Asset("XCU", "Copper", AssetType.METAL, "Industrial metal linked to construction and electrification"));
```

Then add it to the `allAssets` list and add a base price in `seedPriceSeries`.

### Step 3: Improve Real Market Data

The app now tries live APIs before falling back to stored demo rows:

- Binance Spot API for crypto history
- CoinGecko as crypto fallback
- Frankfurter for fiat currency history
- Gold API for latest metal prices

Next improvement: add a paid or authenticated commodity provider if you need full historical metal time series instead of latest live metal prices plus stored chart history.

### Step 4: Add Real Currency Conversion

Recommended API:

- Frankfurter
- ExchangeRate.host
- OpenExchangeRates

First goal:

Show gold price in USD, EUR, GBP, JPY, CHF, CAD, AUD, CNY, INR, and BRL.

### Step 5: Add Crypto Prices

Recommended API:

- Binance Spot API
- CoinGecko

First goal:

Show whether crypto risk appetite is rising or falling compared to metals.

### Step 6: Add Economic Indicators

Recommended API:

- FRED
- World Bank

Start with:

- Inflation
- Interest rates
- Oil price
- USD strength

### Step 7: Add Global Events

Recommended API:

- GDELT

Track keywords:

- mining strike
- sanctions
- war
- export ban
- inflation
- central bank
- energy shortage

### Step 8: Build The Causal Ripple Map

This is the feature that makes RippleLens special.

Example:

```text
Mining strike in South Africa
  -> platinum supply risk increases
  -> platinum price pressure increases
  -> auto catalyst costs may rise
  -> vehicle production cost risk increases
```

### Step 9: Add AI Carefully

AI should explain, not predict with certainty.

Every AI result must include:

- possible causes
- confidence
- sources
- disclaimer

Never write:

```text
Gold will definitely rise.
```

Write:

```text
Gold may be rising because real yields fell and geopolitical risk increased. Confidence: medium.
```

## 7. Fastest Path To Finish A Student Project

Build only these features first:

1. Login is optional, skip it at first.
2. Keep H2 database at first, skip PostgreSQL until later.
3. Keep stored demo data as a fallback.
4. Build the web page.
5. Add one real API.
6. Add one explanation feature.
7. Add one chart.
8. Add the Money Pressure Index.
9. Add a final report/export page if needed.

This gives you a complete project faster than trying to build a huge fintech platform immediately.

## 8. Final Presentation Explanation

Use this sentence:

> RippleLens is not a price tracker. It is an economic intelligence platform that explains why metals move by connecting commodities, currencies, crypto, inflation, and global events.
