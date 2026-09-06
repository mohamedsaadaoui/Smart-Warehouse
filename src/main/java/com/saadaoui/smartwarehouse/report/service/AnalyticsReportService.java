package com.saadaoui.smartwarehouse.report.service;

import com.saadaoui.smartwarehouse.report.dto.InventoryReportResponse;

import java.time.LocalDateTime;

public interface AnalyticsReportService {

    InventoryReportResponse generateInventoryReport(LocalDateTime from, LocalDateTime to);
}
