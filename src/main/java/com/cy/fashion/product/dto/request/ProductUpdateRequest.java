package com.cy.fashion.product.dto.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductUpdateRequest {
    public String id;
    public String brandId;
    public String category;
    public int price;
}
