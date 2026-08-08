package com.saadaoui.smartwarehouse.dashboard.service.impl;

import com.saadaoui.smartwarehouse.category.repository.CategoryRepository;
import com.saadaoui.smartwarehouse.dashboard.dto.CategoryProductCount;
import com.saadaoui.smartwarehouse.dashboard.dto.DashboardSummaryResponse;
import com.saadaoui.smartwarehouse.dashboard.dto.LowStockProductResponse;
import com.saadaoui.smartwarehouse.dashboard.service.DashboardService;
import com.saadaoui.smartwarehouse.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int LOW_STOCK_LIMIT = 5;

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {

        List<LowStockProductResponse> lowStockProducts = productRepository
                .findLowStockProducts(PageRequest.of(0, LOW_STOCK_LIMIT))
                .stream()
                .map(product -> LowStockProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .sku(product.getSku())
                        .quantity(product.getQuantity())
                        .minStock(product.getMinStock())
                        .build())
                .toList();

        List<CategoryProductCount> productsPerCategory = productRepository
                .countProductsByCategory()
                .stream()
                .map(projection -> CategoryProductCount.builder()
                        .name(projection.getName())
                        .productCount(projection.getProductCount())
                        .build())
                .toList();

        return DashboardSummaryResponse.builder()
                .totalProducts(productRepository.count())
                .totalCategories(categoryRepository.count())
                .lowStockCount(productRepository.countLowStock())
                .outOfStockCount(productRepository.countOutOfStock())
                .lowStockProducts(lowStockProducts)
                .productsPerCategory(productsPerCategory)
                .build();
    }

}
