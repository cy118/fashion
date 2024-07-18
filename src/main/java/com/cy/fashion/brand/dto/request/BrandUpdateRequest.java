package com.cy.fashion.brand.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BrandUpdateRequest {

    @NotEmpty(message = "Brand id is mandatory")
    public String id;
    @NotEmpty(message = "Brand name is mandatory")
    public String brandName;
}
