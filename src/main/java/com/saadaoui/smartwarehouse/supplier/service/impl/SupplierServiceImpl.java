package com.saadaoui.smartwarehouse.supplier.service.impl;

import com.saadaoui.smartwarehouse.entity.Supplier;
import com.saadaoui.smartwarehouse.exception.DuplicateResourceException;
import com.saadaoui.smartwarehouse.exception.ResourceNotFoundException;
import com.saadaoui.smartwarehouse.supplier.dto.SupplierRequest;
import com.saadaoui.smartwarehouse.supplier.dto.SupplierResponse;
import com.saadaoui.smartwarehouse.supplier.mapper.SupplierMapper;
import com.saadaoui.smartwarehouse.supplier.repository.SupplierRepository;
import com.saadaoui.smartwarehouse.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierResponse create(SupplierRequest request) {

        if (supplierRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Supplier name already exists");
        }

        Supplier supplier = supplierRepository.save(supplierMapper.toEntity(request));

        log.info("Supplier created: {} ({})", supplier.getName(), supplier.getId());

        return toResponse(supplier, 0);
    }

    @Override
    @Transactional
    public SupplierResponse update(UUID id, SupplierRequest request) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        if (supplierRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Supplier name already exists");
        }

        supplier.setName(request.getName());
        supplier.setContactName(request.getContactName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setActive(request.getActive());

        log.info("Supplier updated: {} ({})", supplier.getName(), supplier.getId());

        return toResponse(supplierRepository.save(supplier), 0);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getById(UUID id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        return toResponse(supplier, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponse> getAll(String search, Pageable pageable) {

        Page<Supplier> suppliers = (search != null && !search.isBlank())
                ? supplierRepository.findByNameContainingIgnoreCase(search.trim(), pageable)
                : supplierRepository.findAll(pageable);

        Map<UUID, Long> productCounts = supplierRepository
                .countProductsBySupplier()
                .stream()
                .collect(Collectors.toMap(
                        com.saadaoui.smartwarehouse.supplier.repository.SupplierRepository.SupplierProductCount::getSupplierId,
                        com.saadaoui.smartwarehouse.supplier.repository.SupplierRepository.SupplierProductCount::getProductCount
                ));

        return suppliers.map(supplier ->
                toResponse(supplier, productCounts.getOrDefault(supplier.getId(), 0L)));
    }

    @Override
    @Transactional
    public void delete(UUID id) {

        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found");
        }

        supplierRepository.deleteById(id);

        log.info("Supplier deleted: {}", id);
    }

    private SupplierResponse toResponse(Supplier supplier, long productCount) {

        SupplierResponse response = supplierMapper.toResponse(supplier);
        response.setProductCount(productCount);

        return response;
    }

}
