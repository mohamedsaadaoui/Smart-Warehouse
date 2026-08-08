package com.saadaoui.smartwarehouse.movement.service.impl;

import com.saadaoui.smartwarehouse.entity.MovementType;
import com.saadaoui.smartwarehouse.entity.Product;
import com.saadaoui.smartwarehouse.entity.StockMovement;
import com.saadaoui.smartwarehouse.exception.InsufficientStockException;
import com.saadaoui.smartwarehouse.exception.ResourceNotFoundException;
import com.saadaoui.smartwarehouse.movement.dto.MovementRequest;
import com.saadaoui.smartwarehouse.movement.dto.MovementResponse;
import com.saadaoui.smartwarehouse.movement.mapper.MovementMapper;
import com.saadaoui.smartwarehouse.movement.repository.StockMovementRepository;
import com.saadaoui.smartwarehouse.movement.service.MovementService;
import com.saadaoui.smartwarehouse.product.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

    private final ProductRepository productRepository;

    private final StockMovementRepository movementRepository;

    private final MovementMapper movementMapper;

    @Override
    @Transactional
    public MovementResponse inbound(MovementRequest request, String username) {

        Product product = findProduct(request.getProductId());
        requirePositiveQuantity(request.getQuantity());

        int before = product.getQuantity();
        int after = before + request.getQuantity();

        return register(product, MovementType.INBOUND, request.getQuantity(),
                before, after, request.getReason(), username);
    }

    @Override
    @Transactional
    public MovementResponse outbound(MovementRequest request, String username) {

        Product product = findProduct(request.getProductId());
        requirePositiveQuantity(request.getQuantity());

        int before = product.getQuantity();

        if (request.getQuantity() > before) {
            throw new InsufficientStockException(
                    "Insufficient stock: only " + before + " units available for " + product.getSku());
        }

        int after = before - request.getQuantity();

        return register(product, MovementType.OUTBOUND, request.getQuantity(),
                before, after, request.getReason(), username);
    }

    @Override
    @Transactional
    public MovementResponse adjust(MovementRequest request, String username) {

        Product product = findProduct(request.getProductId());
        int newTotal = request.getQuantity();

        int before = product.getQuantity();
        int delta = newTotal - before;

        return register(product, MovementType.ADJUSTMENT, newTotal,
                before, newTotal, request.getReason(), username);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovementResponse> getAll(String search, MovementType type, UUID productId,
                                         LocalDateTime from, LocalDateTime to, Pageable pageable) {

        return movementRepository
                .findAll(buildSpecification(search, type, productId, from, to), pageable)
                .map(movementMapper::toResponse);
    }

    private MovementResponse register(Product product, MovementType type, int quantity,
                                      int before, int after, String reason, String username) {

        product.setQuantity(after);
        productRepository.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .type(type)
                .quantity(quantity)
                .beforeQuantity(before)
                .afterQuantity(after)
                .reason(reason)
                .performedBy(username)
                .build();

        StockMovement saved = movementRepository.save(movement);

        log.info("Stock {} for {} ({}): {} -> {} by {}", type, product.getSku(),
                saved.getId(), before, after, username);

        return movementMapper.toResponse(saved);
    }

    private Product findProduct(UUID productId) {

        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private void requirePositiveQuantity(Integer quantity) {

        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
    }

    private Specification<StockMovement> buildSpecification(
            String search, MovementType type, UUID productId,
            LocalDateTime from, LocalDateTime to) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("product").get("name")), pattern),
                        cb.like(cb.lower(root.get("product").get("sku")), pattern)
                ));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (productId != null) {
                predicates.add(cb.equal(root.get("product").get("id"), productId));
            }

            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }

            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
