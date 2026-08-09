package com.saadaoui.smartwarehouse.notification.service.impl;

import com.saadaoui.smartwarehouse.entity.AppSettings;
import com.saadaoui.smartwarehouse.entity.Product;
import com.saadaoui.smartwarehouse.notification.dto.StockAlertResponse;
import com.saadaoui.smartwarehouse.notification.service.NotificationService;
import com.saadaoui.smartwarehouse.product.repository.ProductRepository;
import com.saadaoui.smartwarehouse.settings.repository.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final int MAX_ALERTS = 100;

    private final ProductRepository productRepository;

    private final AppSettingsRepository settingsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockAlertResponse> getAlerts() {

        int threshold = currentThreshold();

        return productRepository
                .findProductsAtOrBelowThreshold(threshold, PageRequest.of(0, MAX_ALERTS))
                .stream()
                .map(NotificationServiceImpl::toAlert)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getAlertCount() {

        return productRepository.countProductsAtOrBelowThreshold(currentThreshold());
    }

    private int currentThreshold() {

        return settingsRepository.findTopByOrderByIdAsc()
                .filter(settings -> Boolean.TRUE.equals(settings.getNotificationsEnabled()))
                .map(AppSettings::getLowStockThreshold)
                .orElse(-1);
    }

    private static StockAlertResponse toAlert(Product product) {

        return StockAlertResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .sku(product.getSku())
                .quantity(product.getQuantity())
                .minStock(product.getMinStock())
                .type(product.getQuantity() == 0 ? "OUT_OF_STOCK" : "LOW_STOCK")
                .createdAt(product.getUpdatedAt())
                .build();
    }

}
