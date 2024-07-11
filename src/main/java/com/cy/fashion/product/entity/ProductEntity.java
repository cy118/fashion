package com.cy.fashion.product.entity;

import com.cy.fashion.brand.entity.BrandEntity;
import com.cy.fashion.product.domain.ProductCategory;
import com.cy.fashion.product.dto.request.ProductCreateRequest;
import com.cy.fashion.product.dto.request.ProductUpdateRequest;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(indexes = {
    @Index(name = "idx_product_brandId", columnList = "brandId"),
    @Index(name = "idx_product_category", columnList = "category"),
    @Index(name = "idx_product_category_price", columnList = "category, price"),
    @Index(name = "idx_product_price", columnList = "price")
})
@NoArgsConstructor
public class ProductEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id", insertable = false, updatable = false)
    private BrandEntity brand;

    @Column(name = "brand_id", nullable = false)
    private UUID brandId;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Column(nullable = false)
    private int price;

    public ProductEntity(UUID id, UUID brandId, ProductCategory category, int price) {
        this.id = id;
        this.brandId = brandId;
        this.category = category;
        this.price = price;
    }

    public static ProductEntity fromCreateRequest(ProductCreateRequest request, ProductCategory category) {
        return new ProductEntity(UUID.randomUUID(), Base62Codec.INSTANCE.decode(request.brandId), category, request.price);
    }

    public static ProductEntity fromUpdateRequest(ProductUpdateRequest request, ProductCategory category) {
        return new ProductEntity(Base62Codec.INSTANCE.decode(request.id), Base62Codec.INSTANCE.decode(request.brandId), category, request.price);
    }
}
