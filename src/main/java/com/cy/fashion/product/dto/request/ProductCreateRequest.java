package com.cy.fashion.product.dto.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductCreateRequest {
    public String brandId;
    public String category;
    public int price;
}
