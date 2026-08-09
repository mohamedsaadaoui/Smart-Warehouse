package com.saadaoui.smartwarehouse.report.service;

import com.saadaoui.smartwarehouse.entity.MovementType;
import com.saadaoui.smartwarehouse.product.dto.ProductStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CsvReportService {

    String exportProducts(String search, UUID categoryId, ProductStatus status);

    String exportMovements(String search, MovementType type, UUID productId,
                           LocalDateTime from, LocalDateTime to);

    String exportInventory();

}
