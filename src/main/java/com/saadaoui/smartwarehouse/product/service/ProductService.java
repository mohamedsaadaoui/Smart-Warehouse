package com.saadaoui.smartwarehouse.product.service;

import com.saadaoui.smartwarehouse.product.dto.ProductRequest;
import com.saadaoui.smartwarehouse.product.dto.ProductResponse;
import com.saadaoui.smartwarehouse.product.dto.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse update(UUID id, ProductRequest request);

    ProductResponse getById(UUID id);

    Page<ProductResponse> getAll(String search, UUID categoryId, ProductStatus status,
                                 Boolean active, Pageable pageable);

    void delete(UUID id);

}
