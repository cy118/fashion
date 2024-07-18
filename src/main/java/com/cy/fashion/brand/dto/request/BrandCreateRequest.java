package com.cy.fashion.brand.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class BrandCreateRequest {

    @NotEmpty(message = "Brand name is mandatory")
    public String brandName;
}
