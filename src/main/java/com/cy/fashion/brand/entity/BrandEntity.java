package com.cy.fashion.brand.entity;

import com.cy.fashion.brand.dto.request.BrandCreateRequest;
import com.cy.fashion.brand.dto.request.BrandUpdateRequest;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table
@NoArgsConstructor
public class BrandEntity {
    @Id
    private UUID id;

    @Column(name = "brand_name", nullable = false, unique = true)
    private String brandName;

    public BrandEntity(UUID id, String brandName) {
        this.id = id;
        this.brandName = brandName;
    }

    public static BrandEntity fromCreateRequest(BrandCreateRequest request) {
        return new BrandEntity(UUID.randomUUID(), request.brandName);
    }

    public static BrandEntity fromUpdateRequest(BrandUpdateRequest request) {
        return new BrandEntity(Base62Codec.INSTANCE.decode(request.id), request.brandName);
    }
}