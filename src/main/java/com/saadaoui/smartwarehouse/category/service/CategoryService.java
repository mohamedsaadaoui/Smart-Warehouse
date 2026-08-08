package com.saadaoui.smartwarehouse.category.service;

import com.saadaoui.smartwarehouse.category.dto.CategoryRequest;
import com.saadaoui.smartwarehouse.category.dto.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(UUID id, CategoryRequest request);

    CategoryResponse getById(UUID id);

    Page<CategoryResponse> getAll(String search, Pageable pageable);

    void delete(UUID id);

}