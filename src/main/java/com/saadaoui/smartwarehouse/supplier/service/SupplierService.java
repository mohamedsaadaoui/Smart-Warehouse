package com.saadaoui.smartwarehouse.supplier.service;

import com.saadaoui.smartwarehouse.supplier.dto.SupplierRequest;
import com.saadaoui.smartwarehouse.supplier.dto.SupplierResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SupplierService {

    SupplierResponse create(SupplierRequest request);

    SupplierResponse update(UUID id, SupplierRequest request);

    SupplierResponse getById(UUID id);

    Page<SupplierResponse> getAll(String search, Pageable pageable);

    void delete(UUID id);

}
