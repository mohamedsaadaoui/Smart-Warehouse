package com.saadaoui.smartwarehouse.supplier.mapper;

import com.saadaoui.smartwarehouse.entity.Supplier;
import com.saadaoui.smartwarehouse.supplier.dto.SupplierRequest;
import com.saadaoui.smartwarehouse.supplier.dto.SupplierResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toEntity(SupplierRequest request);

    @Mapping(target = "productCount", ignore = true)
    SupplierResponse toResponse(Supplier supplier);

}
