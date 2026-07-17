package com.RippleLens.workspace;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecentAssetRequest(@NotBlank @Size(max = 32) String symbol) {
}
