package com.cy.fashion.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductCreateRequest {

    @NotEmpty(message = "Brand id is mandatory")
    public String brandId;

    @NotEmpty(message = "Category is mandatory")
    public String category;

    @NotEmpty(message = "Price is mandatory")
    public int price;
}
