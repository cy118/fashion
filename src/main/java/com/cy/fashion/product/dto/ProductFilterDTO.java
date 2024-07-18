package com.cy.fashion.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "상품 필터", nullable = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterDTO {


    @Schema(description = "필터할 브랜드 id (size가 0일 시 필터하지 않음을 의미)", nullable = true)
    public List<String> brandIds;
    @Schema(description = "필터할 카테고리 (size가 0일 시 필터하지 않음을 의미)", nullable = true, allowableValues = {"상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리"})
    public List<String> categories;
}
