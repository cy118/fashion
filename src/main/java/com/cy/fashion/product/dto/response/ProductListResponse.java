package com.cy.fashion.product.dto.response;

import com.cy.fashion.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    public List<ProductDTO> products;
}
