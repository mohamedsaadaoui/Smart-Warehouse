package com.saadaoui.smartwarehouse.category.mapper;

import com.saadaoui.smartwarehouse.category.dto.CategoryRequest;
import com.saadaoui.smartwarehouse.category.dto.CategoryResponse;
import com.saadaoui.smartwarehouse.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

}