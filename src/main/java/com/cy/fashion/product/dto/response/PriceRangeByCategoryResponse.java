package com.cy.fashion.product.dto.response;

import com.cy.fashion.product.domain.Product;
import com.cy.fashion.product.dto.ProductDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 * 모든 카테고리 별 Product 가 1개씩 담겨있는 경우를 Outfit 이라고 정의한다.
 */
@Schema(description = "카테고리별 최소 가격 상품 리스트와 총액")
@AllArgsConstructor
public class PriceRangeByCategoryResponse {
    public List<ProductDTO> lowest;
    public List<ProductDTO> highest;

    public static PriceRangeByCategoryResponse fromDomain(List<Product> lowest, List<Product> highest) {
        List<ProductDTO> lowestDtos = new ArrayList<>();
        for (Product product : lowest) {
            lowestDtos.add(ProductDTO.fromDomain(product));
        }
        List<ProductDTO> highestDtos = new ArrayList<>();
        for (Product product : highest) {
            highestDtos.add(ProductDTO.fromDomain(product));
        }
        return new PriceRangeByCategoryResponse(lowestDtos, highestDtos);
    }
}