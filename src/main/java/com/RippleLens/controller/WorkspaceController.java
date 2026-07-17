package com.RippleLens.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.service.WorkspaceService;
import com.RippleLens.workspace.RecentAssetRequest;
import com.RippleLens.workspace.RecentAssetResponse;
import com.RippleLens.workspace.UpdateUserPreferencesRequest;
import com.RippleLens.workspace.UserPreferencesResponse;
import com.RippleLens.workspace.WorkspaceSummaryResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping
    public WorkspaceSummaryResponse getWorkspace() {
        return workspaceService.getWorkspace();
    }

    @PatchMapping("/preferences")
    public UserPreferencesResponse updatePreferences(@Valid @RequestBody UpdateUserPreferencesRequest request) {
        return workspaceService.updatePreferences(request);
    }

    @PostMapping("/recent")
    public List<RecentAssetResponse> recordRecentAsset(@Valid @RequestBody RecentAssetRequest request) {
        return workspaceService.recordRecentAsset(request.symbol());
    }
}
