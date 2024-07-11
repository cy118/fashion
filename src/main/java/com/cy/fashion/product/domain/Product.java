package com.cy.fashion.product.domain;

import com.cy.fashion.brand.domain.Brand;
import com.cy.fashion.brand.entity.BrandEntity;
import com.cy.fashion.product.entity.ProductEntity;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Product {
    public String id;
    public Brand brand;
    public ProductCategory category;
    public int price;

    public static Product fromEntity(ProductEntity product) {
        return new Product(
            Base62Codec.INSTANCE.encode(product.getId()), Brand.fromEntity(product.getBrand()),
            product.getCategory(), product.getPrice()
        );
    }

    public static Product fromEntity(ProductEntity product, BrandEntity brand) {
        return new Product(
            Base62Codec.INSTANCE.encode(product.getId()), Brand.fromEntity(brand),
            product.getCategory(), product.getPrice()
        );
    }
}
