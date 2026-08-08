package com.saadaoui.smartwarehouse.movement.service;

import com.saadaoui.smartwarehouse.entity.MovementType;
import com.saadaoui.smartwarehouse.movement.dto.MovementRequest;
import com.saadaoui.smartwarehouse.movement.dto.MovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MovementService {

    MovementResponse inbound(MovementRequest request, String username);

    MovementResponse outbound(MovementRequest request, String username);

    MovementResponse adjust(MovementRequest request, String username);

    Page<MovementResponse> getAll(String search, MovementType type, UUID productId,
                                  LocalDateTime from, LocalDateTime to, Pageable pageable);

}
