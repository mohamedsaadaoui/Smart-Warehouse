package com.saadaoui.smartwarehouse.settings.mapper;

import com.saadaoui.smartwarehouse.entity.AppSettings;
import com.saadaoui.smartwarehouse.settings.dto.SettingsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingsMapper {

    SettingsResponse toResponse(AppSettings settings);

}
