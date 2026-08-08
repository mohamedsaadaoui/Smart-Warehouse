package com.saadaoui.smartwarehouse.movement.mapper;

import com.saadaoui.smartwarehouse.entity.StockMovement;
import com.saadaoui.smartwarehouse.movement.dto.MovementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovementMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "sku", source = "product.sku")
    MovementResponse toResponse(StockMovement movement);

}
