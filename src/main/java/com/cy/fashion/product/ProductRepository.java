package com.cy.fashion.product;

import com.cy.fashion.product.domain.ProductCategory;
import com.cy.fashion.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface  ProductRepository extends JpaRepository<ProductEntity, UUID> {
    @Query("SELECT p FROM ProductEntity p " +
        "JOIN p.brand b " +
        "WHERE p.id IN (" +
        "  SELECT MIN(p2.id) FROM ProductEntity p2 " +
        "  WHERE (:categories IS NULL OR p2.category IN :categories) " +
        "    AND p2.price = (" +
        "      SELECT MIN(p3.price) FROM ProductEntity p3 " +
        "      WHERE p3.category = p2.category " +
        "      AND (:brandIds IS NULL OR p3.brandId IN :brandIds))" +
        "  AND (:brandIds IS NULL OR p2.brandId IN :brandIds) " +
        "  GROUP BY p2.category)")
    List<ProductEntity> findLowestPricedProductsByCategories(List<ProductCategory> categories, List<UUID> brandIds);

    @Query("SELECT p FROM ProductEntity p " +
        "JOIN p.brand b " +
        "WHERE p.brandId = (" +
        "  SELECT p2.brandId FROM ProductEntity p2 " +
        "  WHERE (:categories IS NULL OR p2.category IN :categories) " +
        "  GROUP BY p2.brandId " +
        "  HAVING COUNT(DISTINCT p2.category) = :categoryCount " +
        "  ORDER BY SUM(p2.price) ASC " +
        "  LIMIT 1)" +
        "AND (:categories IS NULL OR p.category IN :categories) " +
        "ORDER BY p.category, p.price ASC")
    List<ProductEntity> findLowestPricedProductsByBrand(List<ProductCategory> categories, int categoryCount);

    @Query("SELECT p FROM ProductEntity p " +
        "WHERE (p.category = :category AND p.price = (" +
        "  SELECT MIN(p2.price) FROM ProductEntity p2 WHERE p2.category = :category" +
        ")) " +
        "UNION " +
        "SELECT p FROM ProductEntity p " +
        "WHERE (p.category = :category AND p.price = (" +
        "  SELECT MAX(p2.price) FROM ProductEntity p2 WHERE p2.category = :category" +
        "))")
    List<ProductEntity> findPriceRangeByCategory(ProductCategory category);

    @Query("SELECT p FROM ProductEntity p " +
        "JOIN p.brand b " +
        "WHERE (:categories IS NULL OR p.category IN :categories)" +
        "AND (:brandIds IS NULL OR p.brandId IN :brandIds)")
    List<ProductEntity> findAllByFilter(List<ProductCategory> categories, List<UUID> brandIds);

    @Query("SELECT p FROM ProductEntity p " +
        "JOIN p.brand b ")
    List<ProductEntity> findAllWithBrand();
}
