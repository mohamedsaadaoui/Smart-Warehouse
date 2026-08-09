package com.saadaoui.smartwarehouse.settings.service.impl;

import com.saadaoui.smartwarehouse.audit.AuditConstants;
import com.saadaoui.smartwarehouse.audit.service.AuditLogService;
import com.saadaoui.smartwarehouse.entity.AppSettings;
import com.saadaoui.smartwarehouse.settings.dto.SettingsRequest;
import com.saadaoui.smartwarehouse.settings.dto.SettingsResponse;
import com.saadaoui.smartwarehouse.settings.mapper.SettingsMapper;
import com.saadaoui.smartwarehouse.settings.repository.AppSettingsRepository;
import com.saadaoui.smartwarehouse.settings.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private final AppSettingsRepository settingsRepository;

    private final SettingsMapper settingsMapper;

    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public SettingsResponse getSettings() {

        return settingsMapper.toResponse(loadOrCreate());
    }

    @Override
    @Transactional
    public SettingsResponse updateSettings(SettingsRequest request) {

        AppSettings settings = loadOrCreate();

        settings.setWarehouseName(request.warehouseName());
        settings.setCurrency(request.currency());
        settings.setLowStockThreshold(request.lowStockThreshold());
        settings.setNotificationsEnabled(request.notificationsEnabled());

        AppSettings saved = settingsRepository.save(settings);

        log.info("Settings updated: warehouse={}, currency={}, threshold={}, notifications={}",
                saved.getWarehouseName(), saved.getCurrency(),
                saved.getLowStockThreshold(), saved.getNotificationsEnabled());

        auditLogService.record(AuditConstants.ACTION_UPDATE, AuditConstants.ENTITY_SETTINGS,
                saved.getId(), "Warehouse settings updated");

        return settingsMapper.toResponse(saved);
    }

    private AppSettings loadOrCreate() {

        return settingsRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> settingsRepository.save(AppSettings.builder()
                        .warehouseName("SmartWarehouse")
                        .currency("USD")
                        .lowStockThreshold(10)
                        .notificationsEnabled(true)
                        .build()));
    }

}
