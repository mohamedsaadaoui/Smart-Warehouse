package com.saadaoui.smartwarehouse.audit.mapper;

import com.saadaoui.smartwarehouse.audit.dto.AuditLogResponse;
import com.saadaoui.smartwarehouse.entity.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    AuditLogResponse toResponse(AuditLog auditLog);

}
