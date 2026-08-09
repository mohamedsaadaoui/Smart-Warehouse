package com.saadaoui.smartwarehouse.settings.controller;

import com.saadaoui.smartwarehouse.settings.dto.SettingsRequest;
import com.saadaoui.smartwarehouse.settings.dto.SettingsResponse;
import com.saadaoui.smartwarehouse.settings.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public SettingsResponse getSettings() {
        return settingsService.getSettings();
    }

    @PutMapping
    public SettingsResponse updateSettings(@Valid @RequestBody SettingsRequest request) {
        return settingsService.updateSettings(request);
    }

}
