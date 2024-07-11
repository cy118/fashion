package com.cy.fashion.product.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum ProductCategory {
    TOP("상의"),
    OUTER("아우터"),
    PANTS("바지"),
    SNEAKERS("스니커즈"),
    BAG("가방"),
    HAT("모자"),
    SOCKS("양말"),
    ACCESSORIES("액세서리");

    private final String categoryName; // Frontend 에서 값 변경 없이 카테고리 이름을 사용할 수 있도록 추가


    private static final Map<String, ProductCategory> lookup = new HashMap<String, ProductCategory>();

    static {
        for (ProductCategory d : ProductCategory.values()) {
            lookup.put(d.getCategoryName(), d);
        }
    }

    public static ProductCategory getByName(String categoryName) {
        return lookup.get(categoryName);
    }
}