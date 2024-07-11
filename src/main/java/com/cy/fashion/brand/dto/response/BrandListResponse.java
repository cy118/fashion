package com.cy.fashion.brand.dto.response;

import com.cy.fashion.brand.domain.Brand;
import com.cy.fashion.brand.dto.BrandDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class BrandListResponse {
    public List<BrandDTO> brands;

    public static BrandListResponse fromDomain(List<Brand> brands) {
        List<BrandDTO> dtos = new ArrayList<>();
        for (Brand brand : brands) {
            dtos.add(BrandDTO.fromDomain(brand));
        }
        return new BrandListResponse(dtos);
    }
}
