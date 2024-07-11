package com.cy.fashion.product.dto.response;

import com.cy.fashion.product.domain.Product;
import com.cy.fashion.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 모든 카테고리 별 Product 가 1개씩 담겨있는 경우를 Outfit 이라고 정의한다.
 */
@Schema(description = "카테고리별 최소 가격 상품 리스트와 총액")
@AllArgsConstructor
public class LowestPriceOutfitResponse {
    public List<ProductDTO> products;
    public int totalPrice;

    public static LowestPriceOutfitResponse fromDomain(List<Product> products) {
        List<ProductDTO> dtos = new ArrayList<>();
        int totalPrice = 0;
        for (Product product : products) {
            dtos.add(ProductDTO.fromDomain(product));
            totalPrice += product.getPrice();
        }
        return new LowestPriceOutfitResponse(dtos, totalPrice);
    }
}
