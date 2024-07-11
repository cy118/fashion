package com.cy.fashion.brand;

import com.cy.fashion.brand.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface  BrandRepository extends JpaRepository<BrandEntity, UUID> {
    BrandEntity findByBrandName(String brandName);
}
