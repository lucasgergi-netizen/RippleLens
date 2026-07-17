package com.RippleLens.watchlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWatchlistItemRequest(@NotBlank @Size(max = 32) String symbol) {
}
