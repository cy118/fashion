package com.cy.fashion.product.dto;

import com.cy.fashion.brand.dto.BrandDTO;
import com.cy.fashion.product.domain.Product;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductDTO {
    public String id;
    public BrandDTO brand;
    public String category;
    public int price;


    public static ProductDTO fromDomain(Product product) {
        return new ProductDTO(
            product.getId(), BrandDTO.fromDomain(product.getBrand()), product.getCategory().getCategoryName(), product.getPrice()
        );
    }
}
