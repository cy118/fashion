package com.cy.fashion.utils.common;

import com.cy.fashion.product.domain.ProductCategory;
import com.cy.fashion.utils.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryValidator {
    public static List<ProductCategory> toCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        List<ProductCategory> validates = new ArrayList<>();
        for (String category : categories) {
            ProductCategory validateCategory = ProductCategory.getByName(category);
            if (validateCategory == null)
                throw new ResourceNotFoundException(String.format("Category [%s] is not a valid category name. Available Categories: 상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리", category));
            validates.add(validateCategory);
        }
        return validates;
    }

    public static ProductCategory toCategory(String category) {
        ProductCategory validateCategory = ProductCategory.getByName(category);
        if (validateCategory == null)
            throw new ResourceNotFoundException(String.format("Category [%s] is not a valid category name. Available Categories: 상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리", category));
        return validateCategory;
    }
}
