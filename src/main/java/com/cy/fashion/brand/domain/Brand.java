package com.cy.fashion.brand.domain;

import com.cy.fashion.brand.entity.BrandEntity;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Brand {
    private String id;
    private String brandName;


    public static Brand fromEntity(BrandEntity brand) {
        return new Brand(
            Base62Codec.INSTANCE.encode(brand.getId()), brand.getBrandName()
        );
    }
}
