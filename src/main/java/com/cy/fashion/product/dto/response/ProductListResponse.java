package com.cy.fashion.product.dto.response;

import com.cy.fashion.product.domain.Product;
import com.cy.fashion.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    public List<ProductDTO> products;

    public static ProductListResponse fromDomain(List<Product> products) {
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            dtos.add(ProductDTO.fromDomain(product));
        }
        return new ProductListResponse(dtos);
    }
}
