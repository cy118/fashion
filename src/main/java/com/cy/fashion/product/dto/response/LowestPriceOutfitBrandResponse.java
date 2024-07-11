package com.cy.fashion.product.dto.response;

import com.cy.fashion.product.domain.Product;
import com.cy.fashion.product.dto.ProductDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "단일 브랜드의 최저 상품 가격")
@AllArgsConstructor
public class LowestPriceOutfitBrandResponse {
    public String brandName;
    public List<ProductDTO> products;
    public Integer totalPrice;

    public static LowestPriceOutfitBrandResponse fromDomain(List<Product> products) {
        List<ProductDTO> dtos = new ArrayList<>();
        int totalPrice = 0;
        for (Product product : products) {
            dtos.add(ProductDTO.fromDomain(product));
            totalPrice += product.getPrice();
        }
        return new LowestPriceOutfitBrandResponse(products.getFirst().getBrand().getBrandName(), dtos, totalPrice);
    }
}
