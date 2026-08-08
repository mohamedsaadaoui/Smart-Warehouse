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
import com.saadaoui.smartwarehouse.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockMovementRepository movementRepository;

    @Mock
    private MovementMapper movementMapper;

    @InjectMocks
    private MovementServiceImpl movementService;

    private Product product;

    private MovementRequest request;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("LED Red")
                .sku("LED-RED-001")
                .price(BigDecimal.TEN)
                .quantity(10)
                .minStock(2)
                .active(true)
                .build();

        request = new MovementRequest();
        request.setProductId(product.getId());
        request.setQuantity(5);
        request.setReason("Test movement");
    }

    @Test
    void inbound_increasesQuantityAndRecordsBeforeAfter() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(movementRepository.save(any(StockMovement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(movementMapper.toResponse(any(StockMovement.class))).thenReturn(MovementResponse.builder().build());

        movementService.inbound(request, "admin@test.com");

        ArgumentCaptor<StockMovement> captor = ArgumentCaptor.forClass(StockMovement.class);
        verify(movementRepository).save(captor.capture());

        StockMovement movement = captor.getValue();
        assertEquals(MovementType.INBOUND, movement.getType());
        assertEquals(5, movement.getQuantity());
        assertEquals(10, movement.getBeforeQuantity());
        assertEquals(15, movement.getAfterQuantity());
        assertEquals(15, product.getQuantity());
        assertEquals("admin@test.com", movement.getPerformedBy());
    }

    @Test
    void outbound_decreasesQuantity() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(movementRepository.save(any(StockMovement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(movementMapper.toResponse(any(StockMovement.class))).thenReturn(MovementResponse.builder().build());

        movementService.outbound(request, "admin@test.com");

        ArgumentCaptor<StockMovement> captor = ArgumentCaptor.forClass(StockMovement.class);
        verify(movementRepository).save(captor.capture());

        assertEquals(MovementType.OUTBOUND, captor.getValue().getType());
        assertEquals(10, captor.getValue().getBeforeQuantity());
        assertEquals(5, captor.getValue().getAfterQuantity());
        assertEquals(5, product.getQuantity());
    }

    @Test
    void outbound_throwsWhenInsufficientStock() {
        request.setQuantity(50);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> movementService.outbound(request, "admin@test.com"));
    }

    @Test
    void adjust_setsNewTotal() {
        request.setQuantity(42);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(movementRepository.save(any(StockMovement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(movementMapper.toResponse(any(StockMovement.class))).thenReturn(MovementResponse.builder().build());

        movementService.adjust(request, "admin@test.com");

        ArgumentCaptor<StockMovement> captor = ArgumentCaptor.forClass(StockMovement.class);
        verify(movementRepository).save(captor.capture());

        StockMovement movement = captor.getValue();
        assertEquals(MovementType.ADJUSTMENT, movement.getType());
        assertEquals(10, movement.getBeforeQuantity());
        assertEquals(42, movement.getAfterQuantity());
        assertEquals(42, product.getQuantity());
    }

    @Test
    void inbound_rejectsZeroQuantity() {
        request.setQuantity(0);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> movementService.inbound(request, "admin@test.com"));
    }

    @Test
    void outbound_throwsWhenProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movementService.outbound(request, "admin@test.com"));
    }
}
