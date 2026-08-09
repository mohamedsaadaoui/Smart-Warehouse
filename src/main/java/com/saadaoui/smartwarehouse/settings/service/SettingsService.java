package com.saadaoui.smartwarehouse.settings.service;

import com.saadaoui.smartwarehouse.settings.dto.SettingsRequest;
import com.saadaoui.smartwarehouse.settings.dto.SettingsResponse;

public interface SettingsService {

    SettingsResponse getSettings();

    SettingsResponse updateSettings(SettingsRequest request);

}
