package com.cy.fashion.brand.dto;

import com.cy.fashion.brand.domain.Brand;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class BrandDTO {
    public String id;
    public String brandName;


    public static BrandDTO fromDomain(Brand brand) {
        if (brand == null) return null;
        return new BrandDTO(
            brand.getId(), brand.getBrandName()
        );
    }
}
