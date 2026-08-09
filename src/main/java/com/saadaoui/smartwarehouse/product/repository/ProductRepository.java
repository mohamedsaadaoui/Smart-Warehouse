package com.saadaoui.smartwarehouse.product.repository;

import com.saadaoui.smartwarehouse.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository
        extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity > 0 AND p.quantity <= p.minStock")
    long countLowStock();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity = 0")
    long countOutOfStock();

    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minStock ORDER BY p.quantity ASC")
    List<Product> findLowStockProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.quantity <= :threshold ORDER BY p.quantity ASC")
    List<Product> findProductsAtOrBelowThreshold(int threshold, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity <= :threshold")
    long countProductsAtOrBelowThreshold(int threshold);

    @Query("""
            SELECT c.name AS name, COUNT(p.id) AS productCount
            FROM Product p JOIN p.category c
            GROUP BY c.name
            ORDER BY productCount DESC
            """)
    List<CategoryCountProjection> countProductsByCategory();

    interface CategoryCountProjection {
        String getName();
        long getProductCount();
    }

}
