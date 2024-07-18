package com.cy.fashion.product.dto.response;

import com.cy.fashion.product.dto.ProductDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

import java.util.List;

@Schema(description = "단일 브랜드의 최저 상품 가격")
@AllArgsConstructor
public class LowestPriceOutfitBrandResponse {
    public String brandName;
    public List<ProductDTO> products;
    public Integer totalPrice;

    public static LowestPriceOutfitBrandResponse fromResult(List<ProductDTO> products) {
        int totalPrice = 0;
        for (ProductDTO product : products) {
            totalPrice += product.price;
        }
        return new LowestPriceOutfitBrandResponse(products.getFirst().brand.brandName, products, totalPrice);
    }
}
