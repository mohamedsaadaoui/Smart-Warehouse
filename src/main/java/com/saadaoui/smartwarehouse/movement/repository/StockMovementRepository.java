package com.saadaoui.smartwarehouse.movement.repository;

import com.saadaoui.smartwarehouse.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, UUID>, JpaSpecificationExecutor<StockMovement> {

    List<StockMovement> findTop5ByOrderByCreatedAtDesc();

    List<StockMovement> findByCreatedAtAfter(java.time.LocalDateTime date);

    Page<StockMovement> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
