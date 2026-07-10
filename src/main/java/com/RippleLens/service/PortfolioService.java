package com.RippleLens.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;
import com.RippleLens.model.PortfolioHolding;
import com.RippleLens.portfolio.CreatePortfolioHoldingRequest;
import com.RippleLens.portfolio.PortfolioAllocationResponse;
import com.RippleLens.portfolio.PortfolioHoldingResponse;
import com.RippleLens.portfolio.PortfolioResponse;
import com.RippleLens.portfolio.UpdatePortfolioHoldingRequest;
import com.RippleLens.repository.AssetRepository;
import com.RippleLens.repository.PortfolioHoldingRepository;

@Service
public class PortfolioService {

    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final AssetRepository assetRepository;
    private final CurrentUserService currentUserService;

    public PortfolioService(
            PortfolioHoldingRepository portfolioHoldingRepository,
            AssetRepository assetRepository,
            CurrentUserService currentUserService) {
        this.portfolioHoldingRepository = portfolioHoldingRepository;
        this.assetRepository = assetRepository;
        this.currentUserService = currentUserService;
    }

    public PortfolioResponse getPortfolio() {
        List<PortfolioHolding> holdings = portfolioHoldingRepository.findBySessionKeyOrderByUpdatedAtDesc(sessionKey());
        return buildPortfolioResponse(holdings);
    }

    @Transactional
    public PortfolioResponse saveHolding(CreatePortfolioHoldingRequest request) {
        Asset asset = findAsset(request.symbol());
        BigDecimal quantity = validateQuantity(request.quantity());
        BigDecimal averageCost = validateAverageCost(request.averageCost(), asset);
        String sessionKey = sessionKey();
        PortfolioHolding holding = portfolioHoldingRepository.findBySessionKeyAndAsset(sessionKey, asset)
                .orElseGet(() -> new PortfolioHolding(sessionKey, asset, quantity, averageCost));
        holding.setQuantity(quantity);
        holding.setAverageCost(averageCost);
        portfolioHoldingRepository.save(holding);
        return getPortfolio();
    }

    @Transactional
    public PortfolioResponse updateHolding(Long id, UpdatePortfolioHoldingRequest request) {
        PortfolioHolding holding = findHolding(id);
        BigDecimal quantity = request.quantity() == null ? holding.getQuantity() : validateQuantity(request.quantity());
        BigDecimal averageCost = request.averageCost() == null
                ? holding.getAverageCost()
                : validateAverageCost(request.averageCost(), holding.getAsset());
        holding.setQuantity(quantity);
        holding.setAverageCost(averageCost);
        portfolioHoldingRepository.save(holding);
        return getPortfolio();
    }

    @Transactional
    public PortfolioResponse deleteHolding(Long id) {
        portfolioHoldingRepository.delete(findHolding(id));
        return getPortfolio();
    }

    private PortfolioResponse buildPortfolioResponse(List<PortfolioHolding> holdings) {
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal dailyChangeAmount = BigDecimal.ZERO;
        Map<AssetCategory, BigDecimal> allocationValues = new LinkedHashMap<>();
        List<PortfolioHoldingResponse> holdingResponses = new ArrayList<>();

        for (PortfolioHolding holding : holdings) {
            Asset asset = holding.getAsset();
            BigDecimal quantity = safe(holding.getQuantity());
            BigDecimal currentPrice = safe(asset.getCurrentPrice());
            BigDecimal currentValue = quantity.multiply(currentPrice, MathContext.DECIMAL64);
            BigDecimal costBasis = quantity.multiply(safe(holding.getAverageCost()), MathContext.DECIMAL64);
            BigDecimal assetDailyChange = safe(asset.getDailyChangePercentage());
            BigDecimal holdingDailyChangeAmount = currentValue
                    .multiply(assetDailyChange, MathContext.DECIMAL64)
                    .divide(BigDecimal.valueOf(100), MathContext.DECIMAL64);

            totalValue = totalValue.add(currentValue);
            totalCost = totalCost.add(costBasis);
            dailyChangeAmount = dailyChangeAmount.add(holdingDailyChangeAmount);
            allocationValues.merge(asset.getCategory(), currentValue, BigDecimal::add);

            holdingResponses.add(toHoldingResponse(
                    holding,
                    currentValue,
                    costBasis,
                    holdingDailyChangeAmount,
                    BigDecimal.ZERO));
        }

        BigDecimal finalTotalValue = totalValue;
        List<PortfolioHoldingResponse> allocatedHoldings = holdingResponses.stream()
                .map(holding -> holdingWithAllocation(holding, percentOf(holding.currentValue(), finalTotalValue)))
                .toList();

        List<PortfolioAllocationResponse> allocations = allocationValues.entrySet()
                .stream()
                .map(entry -> new PortfolioAllocationResponse(
                        entry.getKey().name(),
                        money(entry.getValue()),
                        percentOf(entry.getValue(), finalTotalValue)))
                .toList();

        PortfolioHoldingResponse bestPerformer = allocatedHoldings.stream()
                .max(Comparator.comparing(PortfolioHoldingResponse::unrealizedProfitLossPercentage))
                .orElse(null);
        PortfolioHoldingResponse worstPerformer = allocatedHoldings.stream()
                .min(Comparator.comparing(PortfolioHoldingResponse::unrealizedProfitLossPercentage))
                .orElse(null);
        PortfolioHoldingResponse largestPosition = allocatedHoldings.stream()
                .max(Comparator.comparing(PortfolioHoldingResponse::allocationPercentage))
                .orElse(null);
        PortfolioAllocationResponse dominantAllocation = allocations.stream()
                .max(Comparator.comparing(PortfolioAllocationResponse::allocationPercentage))
                .orElse(null);

        BigDecimal unrealizedProfitLoss = totalValue.subtract(totalCost);
        return new PortfolioResponse(
                money(totalValue),
                money(totalCost),
                money(unrealizedProfitLoss),
                percentChange(totalCost, totalValue),
                money(dailyChangeAmount),
                percentOf(dailyChangeAmount, totalValue),
                allocatedHoldings.size(),
                bestPerformer,
                worstPerformer,
                largestPosition,
                largestPosition == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : largestPosition.allocationPercentage(),
                dominantAllocation == null ? "none" : dominantAllocation.category(),
                exposureSummary(allocations),
                riskLevel(largestPosition, dominantAllocation, allocatedHoldings.size()),
                riskWarning(largestPosition, dominantAllocation, allocatedHoldings.size()),
                allocations,
                allocatedHoldings);
    }

    private PortfolioHoldingResponse toHoldingResponse(
            PortfolioHolding holding,
            BigDecimal currentValue,
            BigDecimal costBasis,
            BigDecimal dailyChangeAmount,
            BigDecimal allocationPercentage) {
        Asset asset = holding.getAsset();
        BigDecimal unrealizedProfitLoss = currentValue.subtract(costBasis);
        return new PortfolioHoldingResponse(
                holding.getId(),
                asset.getSymbol(),
                asset.getName(),
                asset.getCategory().name(),
                decimal(holding.getQuantity(), 8),
                money(holding.getAverageCost()),
                money(asset.getCurrentPrice()),
                money(currentValue),
                money(costBasis),
                money(unrealizedProfitLoss),
                percentChange(costBasis, currentValue),
                decimal(safe(asset.getDailyChangePercentage()), 2),
                money(dailyChangeAmount),
                allocationPercentage,
                asset.getQuoteCurrency(),
                holding.getUpdatedAt());
    }

    private PortfolioHoldingResponse holdingWithAllocation(PortfolioHoldingResponse holding, BigDecimal allocationPercentage) {
        return new PortfolioHoldingResponse(
                holding.id(),
                holding.symbol(),
                holding.assetName(),
                holding.category(),
                holding.quantity(),
                holding.averageCost(),
                holding.currentPrice(),
                holding.currentValue(),
                holding.costBasis(),
                holding.unrealizedProfitLoss(),
                holding.unrealizedProfitLossPercentage(),
                holding.dailyChangePercentage(),
                holding.dailyChangeAmount(),
                allocationPercentage,
                holding.quoteCurrency(),
                holding.updatedAt());
    }

    private Asset findAsset(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asset symbol is required.");
        }
        return assetRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown asset symbol: " + symbol));
    }

    private PortfolioHolding findHolding(Long id) {
        String sessionKey = sessionKey();
        return portfolioHoldingRepository.findById(id)
                .filter(holding -> sessionKey.equals(holding.getSessionKey()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio holding not found."));
    }

    private String sessionKey() {
        return currentUserService.sessionKey();
    }

    private BigDecimal validateQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero.");
        }
        return quantity;
    }

    private BigDecimal validateAverageCost(BigDecimal averageCost, Asset asset) {
        BigDecimal normalizedCost = averageCost == null ? asset.getCurrentPrice() : averageCost;
        if (normalizedCost == null || normalizedCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Average cost cannot be negative.");
        }
        return normalizedCost;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal money(BigDecimal value) {
        return decimal(value, 2);
    }

    private BigDecimal decimal(BigDecimal value, int scale) {
        return safe(value).setScale(scale, RoundingMode.HALF_UP);
    }

    private BigDecimal percentChange(BigDecimal first, BigDecimal second) {
        if (first == null || first.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return second.subtract(first)
                .divide(first, MathContext.DECIMAL64)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percentOf(BigDecimal value, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.divide(total, MathContext.DECIMAL64)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String exposureSummary(List<PortfolioAllocationResponse> allocations) {
        if (allocations.isEmpty()) {
            return "No tracked exposure yet.";
        }
        return allocations.stream()
                .map(allocation -> allocation.category().toLowerCase() + " "
                        + allocation.allocationPercentage().stripTrailingZeros().toPlainString() + "%")
                .reduce((first, second) -> first + ", " + second)
                .orElse("No tracked exposure yet.");
    }

    private String riskLevel(
            PortfolioHoldingResponse largestPosition,
            PortfolioAllocationResponse dominantAllocation,
            int holdingCount) {
        if (holdingCount == 0) {
            return "No holdings";
        }
        BigDecimal largestPositionPercentage = largestPosition == null ? BigDecimal.ZERO : largestPosition.allocationPercentage();
        BigDecimal dominantCategoryPercentage = dominantAllocation == null ? BigDecimal.ZERO : dominantAllocation.allocationPercentage();
        if (largestPositionPercentage.compareTo(BigDecimal.valueOf(60)) >= 0
                || dominantCategoryPercentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "High concentration";
        }
        if (largestPositionPercentage.compareTo(BigDecimal.valueOf(35)) >= 0
                || dominantCategoryPercentage.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "Moderate concentration";
        }
        return "Diversified";
    }

    private String riskWarning(
            PortfolioHoldingResponse largestPosition,
            PortfolioAllocationResponse dominantAllocation,
            int holdingCount) {
        if (holdingCount == 0) {
            return "Add holdings to see concentration, category exposure, and unrealized performance.";
        }
        if (largestPosition != null && largestPosition.allocationPercentage().compareTo(BigDecimal.valueOf(60)) >= 0) {
            return largestPosition.symbol() + " represents " + largestPosition.allocationPercentage()
                    + "% of the tracked portfolio, so one asset can dominate daily movement.";
        }
        if (dominantAllocation != null && dominantAllocation.allocationPercentage().compareTo(BigDecimal.valueOf(80)) >= 0) {
            return formatCategory(dominantAllocation.category()) + " exposure is "
                    + dominantAllocation.allocationPercentage()
                    + "%, so category-specific shocks may dominate the portfolio.";
        }
        if (holdingCount < 3) {
            return "The portfolio has only " + holdingCount
                    + " tracked position(s); add more assets to make concentration risk easier to see.";
        }
        return "No single holding or category currently dominates the tracked portfolio.";
    }

    private String formatCategory(String category) {
        return category == null ? "Unknown" : category.toLowerCase().replace("_", " ");
    }
}
